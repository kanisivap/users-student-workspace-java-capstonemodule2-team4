package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.LoginResponseDto;
import com.techelevator.tenmo.model.TransferDto;

import java.util.List;

public interface TransferDao {
    List<TransferDto> getTransfersByPending(String username);
    List<TransferDto> getTransfers(String username);
    TransferDto getTransferById(int transferId, String username);
    TransferDto createTransfer(TransferDto transferDto, String username);
    TransferDto updateTransfer(TransferDto transferDto, String username);
}
