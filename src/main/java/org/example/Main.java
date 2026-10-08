package org.example;

import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AccountDAO dao = new AccountDAOImpl();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== BANKING MANAGEMENT SYSTEM ===");
            System.out.println("1. Open New Account");
            System.out.println("2. Check Balance");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Fund Transfer");
            System.out.println("6. Print Mini-Statement");
            System.out.println("7. Exit");
            System.out.print("Select an option (1-7): ");

            if (!sc.hasNextInt()) {
                sc.next();
                continue;
            }

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter New Account Number (e.g., 1003): ");
                    long acc = sc.nextLong();

                    System.out.print("Enter Customer ID (e.g., 1): ");
                    int customerId = sc.nextInt();

                    System.out.print("Enter Account Type (SAVINGS / CURRENT): ");
                    String type = sc.next();

                    System.out.print("Enter Initial Deposit Amount: ");
                    BigDecimal initialBal = sc.nextBigDecimal();

                    dao.createAccount(acc, customerId, type, initialBal);
                }
                case 2 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    BigDecimal bal = dao.getBalance(acc);
                    System.out.println(bal != null ? "Current Balance: $" + bal : "Account not found.");
                }
                case 3 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    System.out.print("Enter Amount to Deposit: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    dao.deposit(acc, amount);
                }
                case 4 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    System.out.print("Enter Amount to Withdraw: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    dao.withdraw(acc, amount);
                }
                case 5 -> {
                    System.out.print("Enter Sender Account Number: ");
                    long from = sc.nextLong();
                    System.out.print("Enter Receiver Account Number: ");
                    long to = sc.nextLong();
                    System.out.print("Enter Transfer Amount: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    dao.transfer(from, to, amount);
                }
                case 6 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    dao.printStatement(acc);
                }
                case 7 -> {
                    System.out.println("Exiting system. Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid selection. Try again.");
            }
        }
    }
}