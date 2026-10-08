package com.bank.model;

import java.math.BigDecimal;

public class SavingsAccount extends Account {
    public SavingsAccount(long accountNumber, int customerId, BigDecimal balance, String status) {
        super(accountNumber, customerId, balance, status);
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}