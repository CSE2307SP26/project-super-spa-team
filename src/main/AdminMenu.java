package main;

import java.util.Map;
import java.util.Scanner;

public class AdminMenu {

    private static final int EXIT_SELECTION = 7;
    private static final int MAX_SELECTION = 7;
    private static final String ADMIN_PASSWORD = "admin123";
    private static final int MAX_PASSWORD_ATTEMPTS = 3;

    private BankAccount account;
    private Map<String, BankAccount> allAccounts;
    private Scanner keyboardInput;

    public AdminMenu(BankAccount account, Scanner keyboardInput, Map<String, BankAccount> allAccounts) {
        this.account = account;
        this.keyboardInput = keyboardInput;
        this.allAccounts = allAccounts;
    }

    public boolean authenticate() {
        System.out.println("Admin Menu requires authentication.");
        for (int attempt = 1; attempt <= MAX_PASSWORD_ATTEMPTS; attempt++) {
            System.out.print("Enter admin password (attempt " + attempt + "/" + MAX_PASSWORD_ATTEMPTS + "): ");
            String input = keyboardInput.next();
            if (ADMIN_PASSWORD.equals(input)) {
                System.out.println("Access granted.");
                return true;
            }
            System.out.println("Incorrect password.");
        }
        System.out.println("Access denied. Too many failed attempts.");
        return false;
    }

    public void displayOptions() {
        System.out.println("Admin Menu");
        System.out.println("1. Collect fee from account");
        System.out.println("2. Add interest payment");
        System.out.println("3. View all accounts summary");
        System.out.println("4. Freeze this account");
        System.out.println("5. Unlock this account");
        System.out.println("6. Apply minimum balance fees");
        System.out.println("7. Return to main menu");
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
                performCollection();
                break;
            case 2:
                performInterestPayment();
                break;
            case 3:
                performViewAllAccounts();
                break;
            case 4:
                performFreeze();
                break;
            case 5:
                performUnlock();
                break;
            case 6:
                performApplyMinimumBalanceFees();
                break;
        }
    }

    public void performViewAllAccounts() {
        System.out.println("--- All Accounts Summary ---");
        System.out.printf("%-15s %-12s %s%n", "Account Number", "Balance", "Status");
        for (BankAccount acc : allAccounts.values()) {
            String status = acc.isClosed() ? "Closed" : "Open";
            System.out.printf("%-15s $%-11.2f %s%n",
                acc.getAccountNumber(), acc.getBalance(), status);
        }
        System.out.println("----------------------------");
    }

    public void performCollection() {
        double feeAmount = -1;
        while (feeAmount < 0 || feeAmount > account.getBalance()) {
            System.out.print("Enter fee amount to collect: ");
            feeAmount = keyboardInput.nextDouble();
        }
        try {
            account.collectFee(feeAmount);
            System.out.println("Fee collection successful. New balance: $" + String.format("%.2f", account.getBalance()));
        } catch (IllegalArgumentException e) {
            System.out.println("Fee collection failed.");
        }
    }

    public void performInterestPayment() {
        if (!account.isSavings()) {
            System.out.println("Interest payments can only be applied to Savings accounts.");
            return;
        }
        double amount = -1;
        while (amount < 0) {
            System.out.print("Enter interest payment amount: ");
            amount = keyboardInput.nextDouble();
        }
        try {
            account.addInterest(amount);
            System.out.println("Interest payment successful. New balance: $" + String.format("%.2f", account.getBalance()));
        } catch (IllegalArgumentException e) {
            System.out.println("Interest payment failed.");
        }
    }

    public void performFreeze() {
        if (account.isClosed()) {
            System.out.println("That account is closed and cannot be frozen.");
            return;
        }
        if (account.isFrozen()) {
            System.out.println("Account is already frozen.");
            return;
        }
        String code = account.freeze();
        System.out.println("Account frozen: " + account.getAccountNumber());
        System.out.println("Unlock code: " + code);
    }

    public void performUnlock() {
        if (!account.isFrozen()) {
            System.out.println("That account is not frozen.");
            return;
        }
        System.out.print("Enter unlock code: ");
        String attempt = keyboardInput.next();
        boolean unlocked = account.unlock(attempt);
        if (unlocked) {
            System.out.println("Account unlocked: " + account.getAccountNumber());
        } else {
            System.out.println("Unlock failed: code did not match.");
        }
    }

    public void performApplyMinimumBalanceFees() {
        int feeCount = 0;
        for (BankAccount acc : allAccounts.values()) {
            if (acc.isClosed() || acc.isFrozen()) {
                continue;
            }
            if (acc.isBelowMinimumBalance()) {
                acc.applyMinimumBalanceFee();
                System.out.println("Notice: " + acc.getDisplayName()
                    + " charged a $25.00 minimum balance fee. New balance: $"
                    + String.format("%.2f", acc.getBalance()));
                feeCount++;
            }
        }
        if (feeCount == 0) {
            System.out.println("No accounts are below the minimum balance.");
        }
    }

    public void run() {
        int selection = -1;
        while (selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
    }
}
