package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu {


    private static final int VIEW_HISTORY_SELECTION = 4;
    private static final int WITHDRAWAL_SELECTION = 2;
    private static final int TRANSFER_SELECTION = 7;
    private static final int EXIT_SELECTION = 9;
    private static final int ADMIN_SELECTION = 10;
    private static final int MAX_SELECTION = 10;
    private static final int CLOSE_ACCOUNT_SELECTION = 8;
    private static final String DUMMY_ACCOUNT_NUMBER = "DUMMY-ACC";
    private static final int ADMIN_COOLDOWN_SECONDS = 10;

 private static final int SWITCH_ACTIVE_ACCOUNT_SELECTION = 6;

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

    public String getActiveAccountNumber() {
        return activeAccountNumber;
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Active account: " + getActiveAccount().getDisplayName());

        System.out.println("1. Make a deposit");

        System.out.println("2. Make a withdrawal");
        System.out.println("3. Check Balance");
        System.out.println("4. View Transaction History");
        System.out.println("5. Create additional account");
        System.out.println("6. Switch active account");
        System.out.println("7. Transfer money to dummy account");
        System.out.println("8. Close current account");
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
            case ADMIN_SELECTION:
                performAdminMenu();
                break;
            case 3:
                performCheckBalance();
                break;
            case 5:
                performCreateAdditionalAccount(true);
                break;
            case CLOSE_ACCOUNT_SELECTION:
                performCloseAccount();
                break;
            case SWITCH_ACTIVE_ACCOUNT_SELECTION:
                performSwitchActiveAccount();
        }
    }

    public void performCheckBalance() {
        System.out.println("Your balance is: $" + String.format("%.2f", getActiveAccount().getBalance()));
    }

    public void performDeposit() {
        if (getActiveAccount().isFrozen()) {
            System.out.println("This account is frozen. Unlock it before making a deposit.");
            return;
        }
        double depositAmount = -1;
        while (depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextDouble();
        }

        if (depositAmount == 0 || depositAmount > 5000) {
            System.out.println("Deposit failed: amount must be greater than 0 and no more than $5000.");
            return;
        }

        System.out.print("Confirm deposit of $" + String.format("%.2f", depositAmount) + "? (yes/no): ");
        String confirmation = keyboardInput.next().trim();

        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            try {
                getActiveAccount().deposit(depositAmount);
                System.out.println("Deposit successful. New balance: $" + String.format("%.2f", getActiveAccountBalance()));
            } catch (IllegalArgumentException e) {
                System.out.println("Deposit failed: amount must be greater than 0 and no more than $5000.");
            }
        } else {
            System.out.println("Deposit cancelled.");
        }
    }

    public void performWithdrawal() {
        if (getActiveAccount().isFrozen()) {
            System.out.println("This account is frozen. Unlock it before making a withdrawal.");
            return;
        }
        double withdrawalAmount = -1;
        while (withdrawalAmount < 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawalAmount = keyboardInput.nextDouble();
        }

        if (withdrawalAmount == 0 || withdrawalAmount > 5000 || withdrawalAmount > getActiveAccountBalance()) {
            System.out.println("Withdrawal failed: amount must be greater than 0, no more than $5000, and no more than your balance.");
            return;
        }

        System.out.print("Confirm withdrawal of $" + String.format("%.2f", withdrawalAmount) + "? (yes/no): ");
        String confirmation = keyboardInput.next().trim();

        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            try {
                getActiveAccount().withdraw(withdrawalAmount);
                System.out.println("Withdrawal successful. New balance: $" + String.format("%.2f", getActiveAccountBalance()));
            } catch (IllegalArgumentException e) {
                System.out.println("Withdrawal failed: amount must be greater than 0, no more than $5000, and no more than your balance.");
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

        System.out.println("View history options:");
        System.out.println("1. All");
        System.out.println("2. Money IN (deposits/interest)");
        System.out.println("3. Money OUT (withdrawals/fees)");
        int categoryChoice = getUserSelection(3);

        System.out.println("Sort options:");
        System.out.println("1. No sorting (original order)");
        System.out.println("2. Sort by $ amount (ascending)");
        System.out.println("3. Sort by $ amount (descending)");
        int sortChoice = getUserSelection(3);

        List<String> filtered = new ArrayList<>();
        for (String line : history) {
            if (categoryChoice == 2 && !isMoneyInLine(line)) {
                continue;
            }
            if (categoryChoice == 3 && !isMoneyOutLine(line)) {
                continue;
            }
            filtered.add(line);
        }

        if (filtered.isEmpty()) {
            System.out.println("No transactions found for that filter.");
            return;
        }

        if (sortChoice != 1) {
            sortHistoryByAmount(filtered, sortChoice == 3);
        }

        System.out.println("Transaction History:");
        for (int i = 0; i < filtered.size(); i++) {
            System.out.println((i + 1) + ". " + filtered.get(i));
        }
    }

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("\\$\\s*([0-9]+(?:\\.[0-9]+)?)");

    private static boolean isMoneyInLine(String line) {
        if (line == null) {
            return false;
        }
        return line.startsWith("Deposit:") || line.startsWith("Interest Payment:");
    }

    private static boolean isMoneyOutLine(String line) {
        if (line == null) {
            return false;
        }
        return line.startsWith("Withdrawal:") || line.startsWith("Fee Collected:");
    }

    private static Double extractAmount(String line) {
        if (line == null) {
            return null;
        }
        Matcher m = AMOUNT_PATTERN.matcher(line);
        if (!m.find()) {
            return null;
        }
        try {
            return Double.parseDouble(m.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void sortHistoryByAmount(List<String> lines, boolean descending) {
        List<String> withAmount = new ArrayList<>();
        List<String> withoutAmount = new ArrayList<>();
        for (String line : lines) {
            if (extractAmount(line) == null) {
                withoutAmount.add(line);
            } else {
                withAmount.add(line);
            }
        }

        Comparator<String> byAmount = Comparator.comparing(MainMenu::extractAmount);
        if (descending) {
            byAmount = byAmount.reversed();
        }
        Collections.sort(withAmount, byAmount);

        lines.clear();
        lines.addAll(withAmount);
        lines.addAll(withoutAmount);
    }

    public void performCreateAdditionalAccount(boolean consumeNewline) {
        String number;
        do {
            number = String.format("ACC-%04d", nextAccountSequence++);
        } while (accountsByNumber.containsKey(number));

        BankAccount created = new BankAccount(number);
        accountsByNumber.put(created.getAccountNumber(), created);
        activeAccountNumber = created.getAccountNumber();

        System.out.print("Enter a nickname for this account (or press Enter to skip): ");
        if (consumeNewline) {
            keyboardInput.nextLine();
        }
        String nick = keyboardInput.nextLine();
        created.setNickname(nick);

        System.out.println("Additional account created: " + created.getDisplayName());
        System.out.println("This account is now active. Balance: $" + String.format("%.2f", created.getBalance()));
    }

    public void run() {
        performCreateAdditionalAccount(false);
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

        if (amount == 0 || amount > 5000 || amount > getActiveAccountBalance()) {
            System.out.println("Transfer failed: amount must be greater than 0, no more than $5000, and no more than your balance.");
            return;
        }

        try {
            getActiveAccount().transfer(dummyAccount, amount);
            System.out.println("Transfer completed. Destination: " + dummyAccount.getAccountNumber());
            System.out.println("New balance: $" + String.format("%.2f", getActiveAccountBalance()));
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: amount must be greater than 0, no more than $5000, and no more than your balance.");
        }
    }

    public void performAdminMenu() {
        long now = System.currentTimeMillis();
        if (now < adminLockoutEndTime) {
            long secondsLeft = (adminLockoutEndTime - now) / 1000;
            System.out.println("Admin Menu is locked. Please wait " + secondsLeft + " second(s) before trying again.");
            return;
        }

        AdminMenu adminMenu = new AdminMenu(getActiveAccount(), keyboardInput, accountsByNumber);
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

    public void performCloseAccount(){
        BankAccount activeAccount = getActiveAccount();
        if (activeAccount.isFrozen()) {
            System.out.println("Unable to close account. Account is frozen; unlock it first.");
            return;
        }

        try{
            activeAccount.closeAccount();
            System.out.println("Closed Account " + activeAccount.getDisplayName());
        } catch (IllegalArgumentException e){
            System.out.println("Unable to close account. Account already closed");
        }
    }

    public void performSwitchActiveAccount(){
        // create list of accounts the user can select
        List<BankAccount> selectableAccounts = new ArrayList<>();

        for (BankAccount account : accountsByNumber.values()) {
            
            if (!account.getAccountNumber().equals(DUMMY_ACCOUNT_NUMBER) && !account.isClosed() && !account.getAccountNumber().equals(activeAccountNumber)) {        
                selectableAccounts.add(account);
            }
       }

       if (selectableAccounts.isEmpty()){
        System.out.println("There are no accounts available to switch to.");
        return;
       }

       System.out.println("Select an account to switch to and make active:");
       
       int num = 1;
       for( BankAccount accountOption : selectableAccounts){
            System.out.println(num + ". " + accountOption.getAccountNumber());
            num +=1; 
       }

       int selection = -1;
        
       while (selection < 1 || selection > selectableAccounts.size()) {
            System.out.print("Enter the number of the account you want to make active: ");
            selection = keyboardInput.nextInt();
        }

        BankAccount selectedAccount = selectableAccounts.get(selection - 1);
        activeAccountNumber = selectedAccount.getAccountNumber();

        System.out.println("Active account switched to: " + selectedAccount.getDisplayName());

    }   

}
