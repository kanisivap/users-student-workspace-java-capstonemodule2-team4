package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {
    BigDecimal getBalance(int id);
    boolean addToBalance(int id, BigDecimal amount);
    boolean subtractFromBalance(int id, BigDecimal amount);
}
