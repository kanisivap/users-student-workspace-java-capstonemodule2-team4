package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;

import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            }else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
		BigDecimal balance = accountService.getBalance(currentUser);
        System.out.println("Your current account balance is: $" + balance);
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransfers(currentUser);
        System.out.println("--------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID           From/To            Amount");
        System.out.println("--------------------------------------");
        for (Transfer transfer : transfers) {
            if (accountService.getAccountById(transfer.getAccountFrom()).getUserId() == currentUser.getUser().getId()) {
                User user = accountService.getUserById(accountService.getAccountById(transfer.getAccountTo()).getUserId());
                System.out.println(transfer.getTransferId() + "         To: " + user.getUsername() + "           $" + transfer.getAmount());
            } else if(accountService.getAccountById(transfer.getAccountTo()).getUserId() == currentUser.getUser().getId()) {
                User user = accountService.getUserById(accountService.getAccountById(transfer.getAccountFrom()).getUserId());
                if(transfer.getTransferStatusId() == 3) {
                    System.out.println(transfer.getTransferId() + "         From: " + user.getUsername() + "         $" + transfer.getAmount() + " *Rejected");
                } else {
                    System.out.println(transfer.getTransferId() + "         From: " + user.getUsername() + "         $" + transfer.getAmount());
                }
            }
        }
        System.out.println("Please enter transfer ID to view details (0 to cancel): ");
        int transferId = Integer.parseInt(input.next());
        if(transferId > 0) {
            Transfer details = transferService.getTransferById(currentUser, transferId);
            User from = accountService.getUserById(accountService.getAccountById(details.getAccountFrom()).getUserId());
            User to = accountService.getUserById(accountService.getAccountById(details.getAccountTo()).getUserId());
            while(details == null){
                System.out.println("Please enter a valid transfer ID: ");
                transferId = Integer.parseInt(input.next());
                details = transferService.getTransferById(currentUser, transferId);

            }
            System.out.println("--------------------------------------");
            System.out.println("Transfer Details");
            System.out.println("--------------------------------------");
            System.out.println("ID: " + details.getTransferId() + "\nFrom: " + from.getUsername() + "\nTo: " + to.getUsername() +
                    "\nType: " + details.getTransferTypeDesc() + "\nStatus: " + details.getTransferStatusDesc() + "\nAmount: " + details.getAmount());
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransfersByPending(currentUser);
        System.out.println("--------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID           From/To            Amount");
        System.out.println("--------------------------------------");
        for (Transfer transfer : transfers) {
            if (accountService.getAccountById(transfer.getAccountFrom()).getUserId() == currentUser.getUser().getId()) {
                User user = accountService.getUserById(accountService.getAccountById(transfer.getAccountTo()).getUserId());
                System.out.println(transfer.getTransferId() + "         To: " + user.getUsername() + "           $" + transfer.getAmount());
            } else if(accountService.getAccountById(transfer.getAccountTo()).getUserId() == currentUser.getUser().getId()) {
                User user = accountService.getUserById(accountService.getAccountById(transfer.getAccountFrom()).getUserId());
                System.out.println(transfer.getTransferId() + "         From: " + user.getUsername() + "         $" + transfer.getAmount());
            }
        }
        System.out.println("Please enter transfer ID to view details (0 to cancel): ");
        int id = Integer.parseInt(input.next());

	}

	private void sendBucks() {

        //List all users
        User[] users = accountService.listUsers(currentUser);
        System.out.println("--------------------------------------");
        System.out.println("Users");
        System.out.println("ID           Name");
        System.out.println("--------------------------------------");
        for (User user: users) {
            System.out.println(user.getId() + "         " + user.getUsername());
        }

        //Prompt user to input ID of user to transfer to
        System.out.println("Enter ID of user you are sending to (0 to cancel): ");
        int id = Integer.parseInt(input.next()); //Take user input and store it as new variable, id

        //Continue to prompt user for a valid ID while the input ID is still equivalent to self
        while (id == currentUser.getUser().getId()) {
            System.out.println("Cannot send money to self! Please enter a valid ID.");
            System.out.println("Enter ID of user you are sending to (0 to cancel): ");
            id = Integer.parseInt(input.next());
        }

        BigDecimal balance = accountService.getBalance(currentUser);
        System.out.println("Enter amount: ");
        BigDecimal amount = new BigDecimal(input.next());
        while(amount.compareTo(BigDecimal.ZERO) <= 0){
            System.out.println("Sending amount must be greater than zero.");
            System.out.println("Enter amount: ");
            amount = new BigDecimal(input.next());
        }
		accountService.subtractFromBalance(currentUser, amount);
        if(balance.compareTo(accountService.getBalance(currentUser)) == 0){
            System.out.println("Cannot send more money than is in account.");
        } else {
            accountService.addToBalance(id, amount);
            Transfer log = new Transfer(2, 2, currentUser.getUser().getId(), id, amount);
            transferService.createTransfer(log, currentUser);
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        //List all users
        User[] users = accountService.listUsers(currentUser);
        System.out.println("--------------------------------------");
        System.out.println("Users");
        System.out.println("ID           Name");
        System.out.println("--------------------------------------");
        for (User user: users) {
            System.out.println(user.getId() + "         " + user.getUsername());
        }

        //Prompt user to input ID of user to request from
        System.out.println("Enter ID of user you are requesting from (0 to cancel): ");
        int id = Integer.parseInt(input.next()); //Take user input and store it as new variable, id

        //Continue to prompt user for a valid ID while the input ID is still equivalent to self
        while (id == currentUser.getUser().getId()) {
            System.out.println("Cannot request money from self! Please enter a valid ID.");
            System.out.println("Enter ID of user you are requesting from (0 to cancel): ");
            id = Integer.parseInt(input.next());
        }

        BigDecimal balance = accountService.getBalance(currentUser);
        System.out.println("Enter amount: ");
        BigDecimal amount = new BigDecimal(input.next());
        while(amount.compareTo(BigDecimal.ZERO) <= 0){
            System.out.println("Requesting amount must be greater than zero.");
            System.out.println("Enter amount: ");
            amount = new BigDecimal(input.next());
        }
            Transfer log = new Transfer(1, 1, id, currentUser.getUser().getId(), amount);
            transferService.createTransfer(log, currentUser);
	}
}
