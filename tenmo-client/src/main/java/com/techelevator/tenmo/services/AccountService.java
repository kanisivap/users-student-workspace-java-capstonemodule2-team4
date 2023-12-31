package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.baseUrl = url;
    }

    public User getUserById(int id){
        User user = null;
        try{
            user = restTemplate.getForObject(baseUrl + "/user/" + id, User.class, HttpMethod.GET);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public Account getAccountById(int id) {
        Account account = null;
        try{
            account = restTemplate.getForObject(baseUrl + "/account/" + id, Account.class, HttpMethod.GET);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public BigDecimal getBalance(AuthenticatedUser user) {
        BigDecimal balance = null;
        try {
            balance = restTemplate.getForObject(baseUrl + "/balance/" + user.getUser().getId(), BigDecimal.class, HttpMethod.GET);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public boolean addToBalance(int id, BigDecimal amount){
        boolean success = false;
        try{
            restTemplate.put(baseUrl + "/balance/" + id + "/add", amount, HttpMethod.PUT);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }
    public boolean subtractFromBalance(AuthenticatedUser user, BigDecimal amount){
        boolean success = false;
        try{
            restTemplate.put(baseUrl + "/balance/" + user.getUser().getId() + "/subtract", amount, HttpMethod.PUT);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    private HttpEntity<AuthenticatedUser> createAuthenticatedUserEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(user, headers);
    }

    private HttpEntity<TransferAuth> createTransferAuthEntity(AuthenticatedUser user, Transfer transfer){
        TransferAuth transferAuth = new TransferAuth(user, transfer);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(transferAuth, headers);
    }

    public User[] listUsers(AuthenticatedUser user) {
        User[] users = null;
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "/account",
                    HttpMethod.GET, createAuthenticatedUserEntity(user), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }
}