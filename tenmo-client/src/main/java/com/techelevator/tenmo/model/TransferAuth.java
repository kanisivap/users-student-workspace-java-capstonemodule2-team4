package com.techelevator.tenmo.model;

public class TransferAuth {
    private AuthenticatedUser user = null;
    private Transfer transfer = null;

    public TransferAuth(){}

    public TransferAuth(AuthenticatedUser user, Transfer transfer){
        this.user = user;
        this.transfer = transfer;
    }

    public Transfer getTransfer() {
        return transfer;
    }
}
