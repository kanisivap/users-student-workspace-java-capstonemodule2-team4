package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.List;

public class TransferService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url) {
        this.baseUrl = url;
    }

    public Transfer getTransferById(AuthenticatedUser user, int id){
        HttpEntity<AuthenticatedUser> entity = createAuthenticatedUserEntity(user);
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/transfer/" + id, HttpMethod.GET, entity, Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfer;
    }

    public Transfer[] getTransfers(AuthenticatedUser user) {
        HttpEntity<AuthenticatedUser> entity = createAuthenticatedUserEntity(user);
        Transfer[] transferList = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/transfer", HttpMethod.GET, entity, Transfer[].class);
            transferList = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transferList;
    }
    public Transfer[] getTransfersByPending(AuthenticatedUser user) {
        HttpEntity<AuthenticatedUser> entity = createAuthenticatedUserEntity(user);
        Transfer[] transferList = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/transfer/pending", HttpMethod.GET, entity, Transfer[].class);
            transferList = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transferList;
    }

    public Transfer createTransfer(Transfer transfer) {
        HttpEntity<Transfer> entity = createTransferEntity(transfer);
        Transfer newTransfer = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseUrl + "/transfer", HttpMethod.POST, entity, Transfer.class);
            newTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return newTransfer;

    }



    private HttpEntity<Transfer> createTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<AuthenticatedUser> createAuthenticatedUserEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(user, headers);
    }

}