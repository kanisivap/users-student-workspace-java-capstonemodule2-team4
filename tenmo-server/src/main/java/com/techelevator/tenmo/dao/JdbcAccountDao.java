package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.AccountDto;
import com.techelevator.tenmo.model.LoginResponseDto;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AccountDto getAccountById(int id){
        String sql = "SELECT * FROM account WHERE account_id = ?;";
        AccountDto account = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if(results.next()){
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return account;
    }

    @Override
    public BigDecimal getBalance(int id){
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        BigDecimal result = null;
        try {
            result = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return result;
    }

    public boolean addToBalance(int id, BigDecimal amount){
        String sql = "UPDATE account SET balance = balance + ? WHERE user_id = ?;";
        boolean success = false;
            int numOfRows = 0;
            try {
                numOfRows = jdbcTemplate.update(sql, amount, id);
                if (numOfRows == 0) {
                    throw new DaoException("Zero rows affected, expected one");
                }
                success = true;
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }
            return success;
    }

    public boolean subtractFromBalance(int id, BigDecimal amount){
        String sql = "UPDATE account SET balance = balance - ? WHERE user_id = ?;";
        BigDecimal currentBalance = getBalance(id);
        boolean success = false;
        if(currentBalance.subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
            int numOfRows = 0;
            try {
                numOfRows = jdbcTemplate.update(sql, amount, id);
                if (numOfRows == 0) {
                    throw new DaoException("Zero rows affected, expected one");
                }
                success = true;
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }
        }
        return success;
    }

    public AccountDto mapRowToAccount(SqlRowSet rs){
        AccountDto account = new AccountDto();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
