package main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainMenu {

    private static final int VIEW_HISTORY_SELECTION = 3;
    private static final int EXIT_SELECTION = 4;
    private static final int CREATE_ACCOUNT_SELECTION = 5;
    private static final int ADMIN_SELECTION = 7;
    private static final int MAX_SELECTION = 7;

    private final Map<String, BankAccount> accountsByNumber;
    private String activeAccountNumber;
    private int nextAccountSequence;
    private Scanner keyboardInput;

    public MainMenu() {
        this.accountsByNumber = new LinkedHashMap<>();
        this.nextAccountSequence = 1001;
        this.keyboardInput = new Scanner(System.in);
        seedTestData();
    }

    private BankAccount getActiveAccount() {
        return accountsByNumber.get(activeAccountNumber);
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Active account: " + activeAccountNumber);

        System.out.println("1. Make a deposit");
        System.out.println("2. Check Balance");
        System.out.println("3. View Transaction History");
        System.out.println("4. Exit the app");
        System.out.println("5. Create additional account");
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
            case CREATE_ACCOUNT_SELECTION:
                performCreateAdditionalAccount();
                break;
            case ADMIN_SELECTION:
                AdminMenu adminMenu = new AdminMenu(getActiveAccount(), keyboardInput);
                adminMenu.run();
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
        getActiveAccount().deposit(depositAmount);
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

    private void seedTestData() {
        BankAccount seeded = new BankAccount("CHK1001");
        seeded.deposit(1200.00);
        seeded.deposit(150.00);
        seeded.collectFee(35.00);
        accountsByNumber.put(seeded.getAccountNumber(), seeded);
        activeAccountNumber = seeded.getAccountNumber();
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

}
