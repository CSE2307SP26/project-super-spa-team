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
    private static final int ADMIN_COOLDOWN_SECONDS = 10;

    private Scanner keyboardInput;
    private final Map<String, BankAccount> accountsByNumber;
    private String activeAccountNumber;
    private int nextAccountSequence;
    private final BankAccount dummyAccount;
    private long adminLockoutEndTime = 0;

    public MainMenu() {
        this.accountsByNumber = new LinkedHashMap<>();
        this.nextAccountSequence = 1001;
        this.keyboardInput = new Scanner(System.in);
        this.dummyAccount = new BankAccount(DUMMY_ACCOUNT_NUMBER);
        this.accountsByNumber.put(this.dummyAccount.getAccountNumber(), this.dummyAccount);
    }

    public MainMenu(Scanner scanner) {
        this.accountsByNumber = new LinkedHashMap<>();
        this.nextAccountSequence = 1001;
        this.keyboardInput = scanner;
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
        while (selection < 1 || selection > max) {
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
                performAdminMenu();
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
        System.out.println("Your balance is: $" + String.format("%.2f", getActiveAccount().getBalance()));
    }

    public void performDeposit() {
        double depositAmount = -1;
        while (depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextDouble();
        }
        System.out.print("Confirm deposit of $" + String.format("%.2f", depositAmount) + "? (yes/no): ");
        String confirmation = keyboardInput.next().trim();

        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            try {
                getActiveAccount().deposit(depositAmount);
                System.out.println("Deposit successful. New balance: $" + String.format("%.2f", getActiveAccountBalance()));
            } catch (IllegalArgumentException e) {
                System.out.println("Deposit failed: invalid amount.");
            }
        } else {
            System.out.println("Deposit cancelled.");
        }
    }

    public void performWithdrawal() {
        double withdrawalAmount = -1;
        while (withdrawalAmount < 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawalAmount = keyboardInput.nextDouble();
        }
        System.out.print("Confirm withdrawal of $" + String.format("%.2f", withdrawalAmount) + "? (yes/no): ");
        String confirmation = keyboardInput.next().trim();

        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            try {
                getActiveAccount().withdraw(withdrawalAmount);
                System.out.println("Withdrawal successful. New balance: $" + String.format("%.2f", getActiveAccountBalance()));
            } catch (IllegalArgumentException e) {
                System.out.println("Withdrawal failed: insufficient funds or invalid amount.");
            }
        } else {
            System.out.println("Withdrawal cancelled.");
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

    public void performCreateAdditionalAccount() {
        String number;
        do {
            number = String.format("ACC-%04d", nextAccountSequence++);
        } while (accountsByNumber.containsKey(number));

        BankAccount created = new BankAccount(number);
        accountsByNumber.put(created.getAccountNumber(), created);
        activeAccountNumber = created.getAccountNumber();

        System.out.println("Additional account created: " + created.getAccountNumber());
        System.out.println("This account is now active. Balance: $" + String.format("%.2f", created.getBalance()));
    }

    public void run() {
        performCreateAdditionalAccount();
        int selection = -1;
        while (selection != EXIT_SELECTION) {
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
            System.out.println("New balance: $" + String.format("%.2f", getActiveAccountBalance()));
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: invalid amount or insufficient funds.");
        }
    }

    public void performAdminMenu() {
        long now = System.currentTimeMillis();
        if (now < adminLockoutEndTime) {
            long secondsLeft = (adminLockoutEndTime - now) / 1000;
            System.out.println("Admin Menu is locked. Please wait " + secondsLeft + " second(s) before trying again.");
            return;
        }

        AdminMenu adminMenu = new AdminMenu(getActiveAccount(), keyboardInput);
        if (adminMenu.authenticate()) {
            adminMenu.run();
        } else {
            adminLockoutEndTime = System.currentTimeMillis() + (ADMIN_COOLDOWN_SECONDS * 1000);
            System.out.println("Admin Menu locked for " + ADMIN_COOLDOWN_SECONDS + " seconds.");
        }
    }

    public long getAdminLockoutEndTime() {
        return adminLockoutEndTime;
    }

    public double getActiveAccountBalance() {
        return getActiveAccount().getBalance();
    }

    public void performCloseAccount() {
        BankAccount activeAccount = getActiveAccount();

        try {
            activeAccount.closeAccount();
            System.out.println("Closed Account " + activeAccount.getAccountNumber());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to close account. Account already closed");
        }
    }

}
