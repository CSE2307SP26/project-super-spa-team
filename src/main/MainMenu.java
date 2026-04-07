package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainMenu {


    private static final int VIEW_HISTORY_SELECTION = 4;
    private static final int WITHDRAWAL_SELECTION = 2;
    private static final int TRANSFER_SELECTION = 6;
    private static final int FREEZE_UNLOCK_SELECTION = 8;
    private static final int EXIT_SELECTION = 9;
    private static final int ADMIN_SELECTION = 10;
    private static final int MAX_SELECTION = 10;
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
        System.out.println("8. Freeze/Unlock an account");
        System.out.println("9. Exit the app");
        System.out.println("10. Admin Menu");

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
            case FREEZE_UNLOCK_SELECTION:
                performFreezeOrUnlock();
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
        if (getActiveAccount().isFrozen()) {
            System.out.println("This account is frozen. Unlock it before making a deposit.");
            return;
        }
        double depositAmount = -1;
        while(depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextInt();
        }
        getActiveAccount().deposit(depositAmount);
    }

    public void performWithdrawal() {
        if (getActiveAccount().isFrozen()) {
            System.out.println("This account is frozen. Unlock it before making a withdrawal.");
            return;
        }
        double withdrawalAmount = -1;
        while(withdrawalAmount < 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawalAmount = keyboardInput.nextInt();
        }
        getActiveAccount().withdraw(withdrawalAmount);
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
        if (getActiveAccount().isFrozen()) {
            System.out.println("This account is frozen. Unlock it before making a transfer.");
            return;
        }
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
        if (activeAccount.isFrozen()) {
            System.out.println("Unable to close account. Account is frozen; unlock it first.");
            return;
        }

        try{
            activeAccount.closeAccount();
            System.out.println("Closed Account " + activeAccount.getAccountNumber());
        } catch (IllegalArgumentException e){
            System.out.println("Unable to close account. Account already closed");
        }
    }

    private void performFreezeOrUnlock() {
        System.out.println("1. Freeze an account");
        System.out.println("2. Unlock an account");
        System.out.println("3. Cancel");

        int choice = getUserSelection(3);
        if (choice == 3) {
            return;
        }

        if (choice == 1) {
            BankAccount selected = promptSelectAccount("Select an account to freeze:");
            if (selected == null) {
                return;
            }
            if (selected.isClosed()) {
                System.out.println("That account is closed and cannot be frozen.");
                return;
            }
            String code = selected.freeze();
            System.out.println("Account frozen: " + selected.getAccountNumber());
            System.out.println("Unlock code: " + code);
            return;
        }

        BankAccount selected = promptSelectAccount("Select an account to unlock:");
        if (selected == null) {
            return;
        }
        if (!selected.isFrozen()) {
            System.out.println("That account is not frozen.");
            return;
        }

        System.out.print("Enter unlock code: ");
        String attempt = keyboardInput.next();
        boolean unlocked = selected.unlock(attempt);
        if (unlocked) {
            System.out.println("Account unlocked: " + selected.getAccountNumber());
        } else {
            System.out.println("Unlock failed: code did not match.");
        }
    }

    private BankAccount promptSelectAccount(String prompt) {
        List<BankAccount> selectable = new ArrayList<>();
        for (BankAccount acct : accountsByNumber.values()) {
            if (acct == null) {
                continue;
            }
            if (DUMMY_ACCOUNT_NUMBER.equals(acct.getAccountNumber())) {
                continue;
            }
            selectable.add(acct);
        }

        if (selectable.isEmpty()) {
            System.out.println("No accounts available.");
            return null;
        }

        System.out.println(prompt);
        for (int i = 0; i < selectable.size(); i++) {
            BankAccount acct = selectable.get(i);
            StringBuilder label = new StringBuilder(acct.getAccountNumber());
            if (acct.getAccountNumber().equals(activeAccountNumber)) {
                label.append(" (active)");
            }
            if (acct.isClosed()) {
                label.append(" (closed)");
            } else if (acct.isFrozen()) {
                label.append(" (frozen)");
            }
            System.out.println((i + 1) + ". " + label);
        }

        int selection = -1;
        while (selection < 1 || selection > selectable.size()) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selectable.get(selection - 1);
    }

}
