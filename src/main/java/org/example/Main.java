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
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Fund Transfer");
            System.out.println("5. Print Mini-Statement");
            System.out.println("6. Exit");
            System.out.print("Select an option (1-6): ");

            if (!sc.hasNextInt()) {
                sc.next();
                continue;
            }

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    BigDecimal bal = dao.getBalance(acc);
                    System.out.println(bal != null ? "Current Balance: $" + bal : "Account not found.");
                }
                case 2 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    System.out.print("Enter Amount to Deposit: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    dao.deposit(acc, amount);
                }
                case 3 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    System.out.print("Enter Amount to Withdraw: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    dao.withdraw(acc, amount);
                }
                case 4 -> {
                    System.out.print("Enter Sender Account Number: ");
                    long from = sc.nextLong();
                    System.out.print("Enter Receiver Account Number: ");
                    long to = sc.nextLong();
                    System.out.print("Enter Transfer Amount: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    dao.transfer(from, to, amount);
                }
                case 5 -> {
                    System.out.print("Enter Account Number: ");
                    long acc = sc.nextLong();
                    dao.printStatement(acc);
                }
                case 6 -> {
                    System.out.println("Exiting system. Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid selection. Try again.");
            }
        }
    }
}