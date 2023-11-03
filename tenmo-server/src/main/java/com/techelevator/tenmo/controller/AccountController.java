package com.techelevator.tenmo.controller;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to authenticate users.
 */

@RestController
public class AccountController {

    private final AccountDao accountDao;
    private final UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable int id){
        User user = null;
        try {
            user = userDao.getUserById(id);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed.");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.");
        }
        return user;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    public AccountDto getAccountById(@PathVariable int id) {
        AccountDto account = null;
        try {
            account = accountDao.getAccountById(id);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid user.");
            }

        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not a valid user.");
        }
        return account;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        try {
            BigDecimal balance = accountDao.getBalance(id);
            if (balance == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid user.");
            }
            return balance;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not a valid user.");
        }
    }

    //Method to list user IDs + user names, excluding self
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public List<User> listUsers() {

        return userDao.getUsers(); //call the getUsers method to populate list
    }
    
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/balance/{id}/add", method = RequestMethod.PUT)
    public boolean addToBalance(@RequestBody BigDecimal amount, @PathVariable int id) {
        boolean success = false;
        try{
            accountDao.addToBalance(id, amount);
            success = true;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not a valid user.");
        }
        return success;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/balance/{id}/subtract", method = RequestMethod.PUT)
    public boolean subtractFromBalance(@RequestBody BigDecimal amount, @PathVariable int id) {
        boolean success = false;
        try{
            accountDao.subtractFromBalance(id, amount);
            success = true;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not a valid user.");
        }
        return success;
    }
}
