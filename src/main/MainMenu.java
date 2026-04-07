package main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainMenu {


    private static final int VIEW_HISTORY_SELECTION = 4;
    private static final int WITHDRAWAL_SELECTION = 2;
    private static final int TRANSFER_SELECTION = 6;
    private static final int EXIT_SELECTION = 8;
    private static final int ADMIN_SELECTION = 9;
    private static final int MAX_SELECTION = 9;
    private static final int CLOSE_ACCOUNT_SELECTION = 7;
    private static final String DUMMY_ACCOUNT_NUMBER = "DUMMY-ACC";

    private Scanner keyboardInput;
    private final Map<String, BankAccount> accountsByNumber;
    private String activeAccountNumber;
    private int nextAccountSequence;
    private final BankAccount dummyAccount;

    public MainMenu() {
        this.accountsByNumber = new LinkedHashMap<>();
        this.nextAccountSequence = 1001;
        this.keyboardInput = new Scanner(System.in);
        this.dummyAccount = new BankAccount(DUMMY_ACCOUNT_NUMBER);
        this.accountsByNumber.put(this.dummyAccount.getAccountNumber(), this.dummyAccount);
    }

    private BankAccount getActiveAccount() {
        BankAccount active = accountsByNumber.get(activeAccountNumber);
        if (active == null) {
            throw new IllegalStateException("No active account is selected.");
        }
        return active;
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Active account: " + activeAccountNumber);

        System.out.println("1. Make a deposit");

        System.out.println("2. Make a withdrawal");
        System.out.println("3. Check Balance");
        System.out.println("4. View Transaction History");
        System.out.println("5. Create additional account");
        System.out.println("6. Transfer money to dummy account");
        System.out.println("7. Close current account");
        System.out.println("8. Exit the app");
        System.out.println("9. Admin Menu");

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
            case WITHDRAWAL_SELECTION:
                performWithdrawal();
                break;
            case VIEW_HISTORY_SELECTION:
                performViewTransactionHistory();
                break;
            case TRANSFER_SELECTION:
                performTransfer(); 
                break;
            case ADMIN_SELECTION:
                AdminMenu adminMenu = new AdminMenu(getActiveAccount(), keyboardInput);
                adminMenu.run();
                break;
            case 3:
                performCheckBalance();
                break;
            case 5:
                performCreateAdditionalAccount();
                break;
            case CLOSE_ACCOUNT_SELECTION:
                performCloseAccount();
                break;
        }
    }

    public void performCheckBalance() {
        System.out.println("Your balance is: " + getActiveAccount().getBalance());
    }

    public void performDeposit() {
        double depositAmount = -1;
        while(depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextInt();
        }
        try {
            getActiveAccount().deposit(depositAmount);
            System.out.println("Deposit successful.");
        } catch (IllegalArgumentException e) {
            System.out.println("Deposit failed: amount must be greater than 0 and no more than $5000.");
        }
    }

    public void performWithdrawal() {
        double withdrawalAmount = -1;
        while(withdrawalAmount < 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawalAmount = keyboardInput.nextInt();
        }
        try {
            getActiveAccount().withdraw(withdrawalAmount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
        System.out.println("Withdrawal failed: amount must be greater than 0, no more than $5000, and no more than your balance.");
        }
    }

    public void performViewTransactionHistory() {
        List<String> history = getActiveAccount().getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("Transaction History:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }
    private void performCreateAdditionalAccount() {
        String number;
        do {
            number = String.format("ACC-%04d", nextAccountSequence++);
        } while (accountsByNumber.containsKey(number));

        BankAccount created = new BankAccount(number);
        accountsByNumber.put(created.getAccountNumber(), created);
        activeAccountNumber = created.getAccountNumber();

        System.out.println("Additional account created: " + created.getAccountNumber());
        System.out.println("This account is now active. Balance: " + created.getBalance());
    }

    public void run() {
        performCreateAdditionalAccount();
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

    public void performTransfer() {
        double amount = -1;
        while (amount < 0) {
            System.out.print("How much would you like to transfer to the dummy account?: ");
            amount = keyboardInput.nextDouble();
        }

        try {
            getActiveAccount().transfer(dummyAccount, amount);
            System.out.println("Transfer completed. Destination: " + dummyAccount.getAccountNumber());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: invalid amount or insufficient funds.");
        }
    }

    public void performCloseAccount(){
        BankAccount activeAccount = getActiveAccount();

        try{
            activeAccount.closeAccount();
            System.out.println("Closed Account " + activeAccount.getAccountNumber());
        } catch (IllegalArgumentException e){
            System.out.println("Unable to close account. Account already closed");
        }
    }

}
