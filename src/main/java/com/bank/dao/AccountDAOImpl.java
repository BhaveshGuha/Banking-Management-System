package com.bank.dao;

import com.bank.config.DatabaseConnection;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidAccountException;

import java.math.BigDecimal;
import java.sql.*;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public BigDecimal getBalance(long accountNumber) {
        String sql = "SELECT balance FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }
        } catch (SQLException e) {
            System.err.println("Balance check error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deposit(long accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, 'DEPOSIT', ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql);
                 PreparedStatement txnPs = conn.prepareStatement(txnSql)) {

                updatePs.setBigDecimal(1, amount);
                updatePs.setLong(2, accountNumber);
                int updated = updatePs.executeUpdate();
                if (updated == 0) throw new InvalidAccountException("Account not found.");

                txnPs.setLong(1, accountNumber);
                txnPs.setBigDecimal(2, amount);
                txnPs.executeUpdate();

                conn.commit();
                System.out.println("Successfully deposited $" + amount);
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Deposit failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @Override
    public void withdraw(long accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        String checkSql = "SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, 'WITHDRAWAL', ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql);
                 PreparedStatement updatePs = conn.prepareStatement(updateSql);
                 PreparedStatement txnPs = conn.prepareStatement(txnSql)) {

                checkPs.setLong(1, accountNumber);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) throw new InvalidAccountException("Account not found.");

                BigDecimal currentBal = rs.getBigDecimal("balance");
                if (currentBal.compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient funds. Available: $" + currentBal);
                }

                updatePs.setBigDecimal(1, amount);
                updatePs.setLong(2, accountNumber);
                updatePs.executeUpdate();

                txnPs.setLong(1, accountNumber);
                txnPs.setBigDecimal(2, amount);
                txnPs.executeUpdate();

                conn.commit();
                System.out.println("Successfully withdrew $" + amount);
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Withdrawal failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @Override
    public void transfer(long fromAccount, long toAccount, BigDecimal amount) {
        if (fromAccount == toAccount) {
            System.out.println("Sender and recipient account cannot be the same.");
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        String checkSenderSql = "SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE";
        String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (account_number, transaction_type, amount, target_account_number) VALUES (?, 'TRANSFER', ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkPs = conn.prepareStatement(checkSenderSql);
                 PreparedStatement debitPs = conn.prepareStatement(debitSql);
                 PreparedStatement creditPs = conn.prepareStatement(creditSql);
                 PreparedStatement txnPs = conn.prepareStatement(txnSql)) {

                checkPs.setLong(1, fromAccount);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) throw new InvalidAccountException("Source account not found.");

                BigDecimal senderBal = rs.getBigDecimal("balance");
                if (senderBal.compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient funds for transfer.");
                }

                debitPs.setBigDecimal(1, amount);
                debitPs.setLong(2, fromAccount);
                debitPs.executeUpdate();

                creditPs.setBigDecimal(1, amount);
                creditPs.setLong(2, toAccount);
                int credited = creditPs.executeUpdate();
                if (credited == 0) throw new InvalidAccountException("Destination account not found.");

                txnPs.setLong(1, fromAccount);
                txnPs.setBigDecimal(2, amount);
                txnPs.setLong(3, toAccount);
                txnPs.executeUpdate();

                conn.commit();
                System.out.println("Transferred $" + amount + " to account " + toAccount);
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Transfer failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    @Override
    public void printStatement(long accountNumber) {
        String sql = "SELECT transaction_id, transaction_type, amount, target_account_number, timestamp " +
                "FROM transactions WHERE account_number = ? ORDER BY timestamp DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Mini Statement for Account: " + accountNumber + " ---");
            System.out.printf("%-6s | %-12s | %-10s | %-12s | %-20s\n", "ID", "Type", "Amount", "Target Acc", "Timestamp");
            System.out.println("----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-6d | %-12s | $%-9.2f | %-12s | %-20s\n",
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
                        rs.getBigDecimal("amount"),
                        rs.getObject("target_account_number") != null ? rs.getLong("target_account_number") : "-",
                        rs.getTimestamp("timestamp"));
            }
        } catch (SQLException e) {
            System.err.println("Statement error: " + e.getMessage());
        }
    }
}