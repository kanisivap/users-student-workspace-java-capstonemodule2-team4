package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class AccountDto {
    private int accountId;
    private int userId;
    private BigDecimal balance;

    public AccountDto(){}

    public AccountDto(int userId, BigDecimal balance){
        this.userId = userId;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
