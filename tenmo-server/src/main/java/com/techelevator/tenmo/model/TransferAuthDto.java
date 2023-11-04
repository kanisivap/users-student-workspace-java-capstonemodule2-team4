package com.techelevator.tenmo.model;

public class TransferAuthDto {
    private LoginResponseDto user = null;
    private TransferDto transfer = null;

    public TransferAuthDto(LoginResponseDto user, TransferDto transfer){
        this.user = user;
        this.transfer = transfer;
    }

    public TransferDto getTransfer() {
        return transfer;
    }

    public LoginResponseDto getUser() {
        return user;
    }
}
