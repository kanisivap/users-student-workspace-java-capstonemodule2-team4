package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;

public interface TransferDao {
    TransferDto getTransferById(int transferId);
    TransferDto createTransfer(TransferDto transferDto);
}
