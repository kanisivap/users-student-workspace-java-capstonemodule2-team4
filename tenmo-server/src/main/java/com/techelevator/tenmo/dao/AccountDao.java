package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountDto;

import java.math.BigDecimal;

public interface AccountDao {
    AccountDto getAccountById(int id);
    BigDecimal getBalance(int id);
    boolean addToBalance(int id, BigDecimal amount);
    boolean subtractFromBalance(int id, BigDecimal amount);
}
