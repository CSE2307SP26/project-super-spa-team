package main;

import java.util.Scanner;

public class AdminMenu {

    private static final int EXIT_SELECTION = 3;
    private static final int MAX_SELECTION = 3;
    private static final String ADMIN_PASSWORD = "admin123";
    private static final int MAX_PASSWORD_ATTEMPTS = 3;

    private BankAccount account;
    private Scanner keyboardInput;

    public AdminMenu(BankAccount account, Scanner keyboardInput) {
        this.account = account;
        this.keyboardInput = keyboardInput;
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
        System.out.println("3. Return to main menu");
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
        }
    }

    public void performCollection() {
        double feeAmount = -1;
        while (feeAmount < 0 || feeAmount > account.getBalance()) {
            System.out.print("Enter fee amount to collect: ");
            feeAmount = keyboardInput.nextInt();
        }
        account.collectFee(feeAmount);
    }

    public void performInterestPayment() {
        double amount = -1;
        while (amount < 0) {
            System.out.print("Enter interest payment amount: ");
            amount = keyboardInput.nextInt();
        }
        account.addInterest(amount);
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
