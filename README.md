# 🏦 Banking Management System

A desktop-based banking application built with **Java (Swing)**, **JDBC**, and **MySQL**. It provides essential banking operations such as account creation, deposits, withdrawals, fund transfers, and transaction history tracking with ACID-compliant database transactions.

---

## 📌 Features

- **Account Management**: Create savings or current accounts linked to registered customers.
- **Deposit & Withdrawal**: Perform real-time balance updates with negative-value and boundary checks.
- **Atomic Fund Transfers**: Secure transfers between accounts using JDBC transaction management (`setAutoCommit(false)`, `commit()`, and `rollback()`) with pessimistic row locking (`FOR UPDATE`).
- **Transaction History**: Automatically records all deposits, withdrawals, and transfers with timestamps.
- **Dual Interface**:
  - **Desktop GUI**: Clean Java Swing interface with tabular views and forms.
  - **CLI Mode**: Interactive terminal menu for console operations.
- **OOP Architecture**: Modular design following the Data Access Object (DAO) pattern, custom exception handling, abstraction, and polymorphism.

---

## 🛠️ Tech Stack

- **Language**: Java 17+
- **GUI Framework**: Java Swing / AWT
- **Database**: MySQL 8.x
- **Connectivity**: JDBC (MySQL Connector/J)
- **Build Tool**: Apache Maven

---

## 📂 Project Structure

```text
banking-management-system/
├── src/
│   └── main/
│       └── java/
│           ├── com/bank/
│           │   ├── config/          # Database connection factory
│           │   │   └── DatabaseConnection.java
│           │   ├── dao/             # Data Access Object interface & implementation
│           │   │   ├── AccountDAO.java
│           │   │   └── AccountDAOImpl.java
│           │   ├── exception/       # Custom checked banking exceptions
│           │   │   ├── InsufficientBalanceException.java
│           │   │   └── InvalidAccountException.java
│           │   └── model/           # Domain entities (Account, SavingsAccount, etc.)
│           │       ├── Account.java
│           │       └── SavingsAccount.java
│           └── org/example/
│               ├── BankAppGUI.java  # Swing Desktop Interface
│               └── Main.java        # CLI Entry Point
├── pom.xml                          # Maven build & dependency configuration
└── README.md
```
## 🗄️ Database Setup
- Open your MySQL client (Workbench or Terminal).
- Execute the following script to set up the database and test seed data:

```
CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;

-- 1. Customers Table
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Accounts Table
CREATE TABLE accounts (
    account_number BIGINT PRIMARY KEY,
    customer_id INT NOT NULL,
    account_type ENUM('SAVINGS', 'CURRENT') NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'SUSPENDED', 'CLOSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_account 
        FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) 
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Transactions Table
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number BIGINT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    target_account_number BIGINT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_transaction 
        FOREIGN KEY (account_number) 
        REFERENCES accounts(account_number) 
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- Starter Seed Data
INSERT INTO customers (customer_id, full_name, email, phone) VALUES 
(1, 'Alex Mercer', 'alex.mercer@example.com', '9876543210'),
(2, 'Sarah Connor', 'sarah.connor@example.com', '9123456780');

INSERT INTO accounts (account_number, customer_id, account_type, balance, status) VALUES 
(1001, 1, 'SAVINGS', 5000.00, 'ACTIVE'),
(1002, 1, 'CURRENT', 2500.00, 'ACTIVE'),
(1003, 2, 'SAVINGS', 10000.00, 'ACTIVE');

INSERT INTO transactions (account_number, transaction_type, amount, target_account_number) VALUES 
(1001, 'DEPOSIT', 5000.00, NULL),
(1002, 'DEPOSIT', 2500.00, NULL),
(1003, 'DEPOSIT', 10000.00, NULL);
```
## ⚙️ Installation & Configuration
1. Clone the Repository
```
git clone [https://github.com/YOUR_USERNAME/BankingManagementSystem.git](https://github.com/YOUR_USERNAME/BankingManagementSystem.git)
cd BankingManagementSystem
```

2. Configure Database Credentials
```
private static final String URL = "jdbc:mysql://localhost:3306/banking_db?useSSL=false&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "your_actual_password";
```

3. Build the Project
```
mvn clean compile
```

## 🚀 Running the Application
Option A: Desktop GUI (Swing)
Run the GUI application class:

```
mvn exec:java -Dexec.mainClass="org.example.BankAppGUI"
```
Option B: Interactive CLI
Run the console application class:

```
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## 🔒 Transaction Safety & Concurrency
- Pessimistic Row Locking: All debit operations execute SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE to lock the row and prevent race conditions.
- ACID Guarantees: Auto-commit is disabled during transfer workflows; any failure triggers an automatic conn.rollback() inside the catch block to ensure funds are never lost or duplicated.
- Custom Exceptions: Uses InsufficientBalanceException and InvalidAccountException to enforce business invariants before mutating state.
