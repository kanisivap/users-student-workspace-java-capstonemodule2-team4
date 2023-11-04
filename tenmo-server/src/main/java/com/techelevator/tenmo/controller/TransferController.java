package com.techelevator.tenmo.controller;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.TransferDao;
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
import java.security.Principal;
import java.util.List;

/**
 * Controller to authenticate users.
 */

@RestController
public class TransferController {

    private final TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/transfer/{transferId}", method = RequestMethod.GET)
    public TransferDto getTransferById(@PathVariable int transferId, Principal principal){
        TransferDto transfer = null;
        try{
            transfer = transferDao.getTransferById(transferId, principal.getName());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer list not found.");
        }

        return transfer;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/transfer/pending", method = RequestMethod.GET)
    public List<TransferDto> getTransfersByPending(Principal principal) {
        List<TransferDto> transferDtoList = null;
        try {
            transferDtoList = transferDao.getTransfersByPending(principal.getName());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer list not found.");
        }

        return transferDtoList;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public List<TransferDto> getTransfers(Principal principal) {
        List<TransferDto> transferDtoList = null;
        try {
            transferDtoList = transferDao.getTransfers(principal.getName());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer list not found.");
        }

        return transferDtoList;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public TransferDto createTransfer(@Valid @RequestBody TransferAuthDto transferAuthDto, Principal principal) {
        TransferDto transfer = transferAuthDto.getTransfer();
        TransferDto newTransfer = null;
        try {
            newTransfer = transferDao.createTransfer(transfer, principal.getName());
            if (newTransfer == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer creation failed.");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer creation failed.");
        }
        return newTransfer;
    }


}