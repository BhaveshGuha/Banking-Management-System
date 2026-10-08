package com.bank.dao;

import java.math.BigDecimal;

public interface AccountDAO {
    BigDecimal getBalance(long accountNumber);
    void deposit(long accountNumber, BigDecimal amount);
    void withdraw(long accountNumber, BigDecimal amount);
    void transfer(long fromAccount, long toAccount, BigDecimal amount);
    void printStatement(long accountNumber);

    void createAccount(long accountNumber, int customerId, String accountType, BigDecimal initialDeposit);
}