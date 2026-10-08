package com.bank.model;

import java.math.BigDecimal;

public abstract class Account {
    private long accountNumber;
    private int customerId;
    private BigDecimal balance;
    private String status;

    public Account(long accountNumber, int customerId, BigDecimal balance, String status) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
        this.status = status;
    }

    public long getAccountNumber() { return accountNumber; }
    public int getCustomerId() { return customerId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public abstract String getAccountType();
}