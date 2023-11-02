package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;

import java.util.List;

public interface TransferDao {
    List<TransferDto> getTransfers();
    TransferDto getTransferById(int transferId);
    TransferDto createTransfer(TransferDto transferDto);
}
