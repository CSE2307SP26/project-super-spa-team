package main;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final int VIEW_HISTORY_SELECTION = 3;
    private static final int WITHDRAWAL_SELECTION = 4;
    private static final int TRANSFER_SELECTION = 5;
    private static final int EXIT_SELECTION = 6;
    private static final int ADMIN_SELECTION = 7;
    private static final int MAX_SELECTION = 7;

    private BankAccount userAccount;
    private BankAccount secondAccount; // to receive transfers
    private Scanner keyboardInput;

    public MainMenu() {
        this.userAccount = new BankAccount();
        this.secondAccount = new BankAccount();
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");

        System.out.println("1. Make a deposit");
        System.out.println("2. Check Balance");
        System.out.println("3. View Transaction History");
        System.out.println("4. Make a withdrawal");
        System.out.println("5. Transfer money to second account");
        System.out.println("6. Exit the app");
        System.out.println("7. Admin Menu");       

    }

    public int getUserSelection(int max) {
        int selection = -1;
        while(selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case 1:
                performDeposit();
                break;
            case 2:
                performCheckBalance();
                break;
            case VIEW_HISTORY_SELECTION:
                performViewTransactionHistory();
                break;
            case WITHDRAWAL_SELECTION:
                performWithdrawal();
                break;
            case TRANSFER_SELECTION:
                performTransfer(); 
                break;
            case ADMIN_SELECTION:
                AdminMenu adminMenu = new AdminMenu(userAccount, keyboardInput);
                adminMenu.run();
                break;
        }
    }

    public void performCheckBalance() {
        System.out.println("Your balance is: " + userAccount.getBalance());
    }

    public void performDeposit() {
        double depositAmount = -1;
        while(depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextInt();
        }
        userAccount.deposit(depositAmount);
    }

    public void performViewTransactionHistory() {
        List<String> history = userAccount.getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("Transaction History:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    public void performWithdrawal() {
        double withdrawalAmount = -1;
        while(withdrawalAmount < 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawalAmount = keyboardInput.nextInt();
        }
        userAccount.withdraw(withdrawalAmount);
    }

    public void run() {
        int selection = -1;
        while(selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
    }

    public static void main(String[] args) {
        MainMenu bankApp = new MainMenu();
        bankApp.run();
    }

    public void performTransfer(){

        System.out.println("How much would you like to transfer to the second account?: ");
        double amount = keyboardInput.nextDouble();
        userAccount.transfer(secondAccount, amount);
        System.out.println("Transfer completed.");
    }

}
