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

    private static final int DEPOSIT_SELECTION = 1;
    private static final int WITHDRAWAL_SELECTION = 2;
    private static final int CHECK_BALANCE_SELECTION = 3;
    private static final int VIEW_HISTORY_SELECTION = 4;
    private static final int CREATE_ACCOUNT_SELECTION = 5;
    private static final int SWITCH_ACTIVE_ACCOUNT_SELECTION = 6;
    private static final int TRANSFER_SELECTION = 7;
    private static final int CLOSE_ACCOUNT_SELECTION = 8;
    private static final int EXIT_SELECTION = 9;
    private static final int ADMIN_SELECTION = 10;
    private static final int MAX_SELECTION = 10;
    private static final double MAX_TRANSACTION_LIMIT = 5000.0;
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

    public String getActiveAccountNumber() {
        return activeAccountNumber;
    }

    public long getAdminLockoutEndTime() {
        return adminLockoutEndTime;
    }

    public double getActiveAccountBalance() {
        return getActiveAccount().getBalance();
    }

    public double getActiveAccountOutstandingLoan() {
        return getActiveAccount().getOutstandingLoan();
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("Active account: " + getActiveAccount().getDisplayName());
        System.out.println("Account Status: " + getActiveAccount().getAccountStatus());
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
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case DEPOSIT_SELECTION:
                performDeposit();
                break;
            case WITHDRAWAL_SELECTION:
                performWithdrawal();
                break;
            case CHECK_BALANCE_SELECTION:
                performCheckBalance();
                break;
            case VIEW_HISTORY_SELECTION:
                performViewTransactionHistory();
                break;
            case CREATE_ACCOUNT_SELECTION:
                performCreateAdditionalAccount(true);
                break;
            case SWITCH_ACTIVE_ACCOUNT_SELECTION:
                performSwitchActiveAccount();
                break;
            case TRANSFER_SELECTION:
                performTransfer();
                break;
            case CLOSE_ACCOUNT_SELECTION:
                performCloseAccount();
                break;
            case EXIT_SELECTION:
                break;
            case ADMIN_SELECTION:
                performAdminMenu();
                break;
        }
    }

    public void performRequestLoan() {
        if (isActiveAccountFrozen("loan request")) {
            return;
        }
        if (getActiveAccount().isClosed()) {
            System.out.println("This account is closed. You cannot request a loan.");
            return;
        }

        double amount = promptForPositiveAmount("How much would you like to borrow?: ");

        if (isAmountInvalid(amount, false)) {
            System.out.println("Loan request failed: amount must be greater than 0 and no more than $" + String.format("%.0f", MAX_TRANSACTION_LIMIT) + ".");
            return;
        }

        if (confirmAction("loan of $" + String.format("%.2f", amount))) {
            try {
                getActiveAccount().requestLoan(amount);
                System.out.println("Loan approved.");
                System.out.println("New balance: $" + String.format("%.2f", getActiveAccountBalance()));
                System.out.println("Your total loan is now: $" + String.format("%.2f", getActiveAccount().getOutstandingLoan()));
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Loan request failed.");
            }
        } else {
            System.out.println("Loan cancelled.");
        }
    }

    public void performCheckBalance() {
        System.out.println("Your balance is: $" + String.format("%.2f", getActiveAccount().getBalance()));
    }

    private boolean isActiveAccountFrozen(String operationName) {
        if (getActiveAccount().isFrozen()) {
            System.out.println("This account is frozen. Unlock it before making a " + operationName + ".");
            return true;
        }
        return false;
    }

    private double promptForPositiveAmount(String prompt) {
        double amount = -1;
        while (amount < 0) {
            System.out.print(prompt);
            amount = keyboardInput.nextDouble();
        }
        return amount;
    }

    private boolean isAmountInvalid(double amount, boolean checkBalance) {
        if (amount == 0 || amount > MAX_TRANSACTION_LIMIT) {
            return true;
        }
        if (checkBalance && amount > getActiveAccountBalance()) {
            return true;
        }
        return false;
    }

    private boolean confirmAction(String description) {
        System.out.print("Confirm " + description + "? (yes/no): ");
        String confirmation = keyboardInput.next().trim();
        return confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y");
    }

    public void performDeposit() {
        if (isActiveAccountFrozen("deposit")) {
            return;
        }

        double depositAmount = promptForPositiveAmount("How much would you like to deposit: ");

        if (isAmountInvalid(depositAmount, false)) {
            System.out.println("Deposit failed: amount must be greater than 0 and no more than $5000.");
            return;
        }

        if (confirmAction("deposit of $" + String.format("%.2f", depositAmount))) {
            try {
                BankAccount.AccountStatus oldStatus = getActiveAccount().getAccountStatus();
                double bonus = getActiveAccount().deposit(depositAmount);
                System.out.println("Deposit successful. $"
                    + String.format("%.2f", bonus)
                    + " deposit bonus due to "
                    + oldStatus.label()
                    + " status. New balance: $"
                    + String.format("%.2f", getActiveAccountBalance()));
            } catch (IllegalArgumentException e) {
                System.out.println("Deposit failed: amount must be greater than 0 and no more than $5000.");
            }
        } else {
            System.out.println("Deposit cancelled.");
        }
    }

    public void performPayTowardLoan() {
        if (isActiveAccountFrozen("loan payment")) {
            return;
        }
        if (getActiveAccount().isClosed()) {
            System.out.println("This account is closed. You cannot pay toward a loan.");
            return;
        }
        if (getActiveAccount().getOutstandingLoan() <= 0) {
            System.out.println("You have no loan balance to pay.");
            return;
        }

        double amount = promptForPositiveAmount("How much would you like to pay toward your loan?: ");

        if (isAmountInvalid(amount, true)) {
            System.out.println("Payment failed: amount must be greater than 0, no more than $" + String.format("%.0f", MAX_TRANSACTION_LIMIT) + ", and no more than your balance.");
            return;
        }
        if (amount > getActiveAccount().getOutstandingLoan()) {
            System.out.println("Payment failed: amount cannot exceed your outstanding loan.");
            return;
        }

        if (confirmAction("loan payment of $" + String.format("%.2f", amount))) {
            try {
                getActiveAccount().payTowardLoan(amount);
                System.out.println("Loan payment successful.");
                System.out.println("New balance: $" + String.format("%.2f", getActiveAccountBalance()));
                System.out.println("Your remaining loan is: $" + String.format("%.2f", getActiveAccount().getOutstandingLoan()));
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Loan payment failed.");
            }
        } else {
            System.out.println("Loan payment cancelled.");
        }
    }

    public void performWithdrawal() {
        if (isActiveAccountFrozen("withdrawal")) {
            return;
        }

        double withdrawalAmount = promptForPositiveAmount("How much would you like to withdraw: ");

        if (isAmountInvalid(withdrawalAmount, true)) {
            System.out.println("Withdrawal failed: amount must be greater than 0, no more than $5000, and no more than your balance.");
            return;
        }

        if (confirmAction("withdrawal of $" + String.format("%.2f", withdrawalAmount))) {
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

    public void performTransfer() {
        if (isActiveAccountFrozen("transfer")) {
            return;
        }

        double amount = promptForPositiveAmount("How much would you like to transfer to the dummy account?: ");

        if (isAmountInvalid(amount, true)) {
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

    public void performViewTransactionHistory() {
        List<String> history = getActiveAccount().getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        int categoryChoice = promptCategoryChoice();
        int sortChoice = promptSortChoice();

        List<String> filtered = filterHistory(history, categoryChoice);
        if (filtered.isEmpty()) {
            System.out.println("No transactions found for that filter.");
            return;
        }

        if (sortChoice != 1) {
            sortHistoryByAmount(filtered, sortChoice == 3);
        }

        displayFilteredHistory(filtered);
    }

    private int promptCategoryChoice() {
        System.out.println("View history options:");
        System.out.println("1. All");
        System.out.println("2. Money IN (deposits/interest)");
        System.out.println("3. Money OUT (withdrawals/fees)");
        return getUserSelection(3);
    }

    private int promptSortChoice() {
        System.out.println("Sort options:");
        System.out.println("1. No sorting (original order)");
        System.out.println("2. Sort by $ amount (ascending)");
        System.out.println("3. Sort by $ amount (descending)");
        return getUserSelection(3);
    }

    private List<String> filterHistory(List<String> history, int categoryChoice) {
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
        return filtered;
    }

    private void displayFilteredHistory(List<String> entries) {
        System.out.println("Transaction History:");
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i));
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
        return line.startsWith("Withdrawal:") || line.startsWith("Fee Collected:")
            || line.startsWith("Loan payment:");
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

        if (consumeNewline) {
            keyboardInput.nextLine();
        }

        System.out.println("Select account type:");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        int typeChoice = getUserSelection(2);
        keyboardInput.nextLine(); // consume newline left by nextInt
        BankAccount.AccountType chosenType =
            (typeChoice == 2) ? BankAccount.AccountType.SAVINGS : BankAccount.AccountType.CHECKING;

        BankAccount created = new BankAccount(number, chosenType);
        accountsByNumber.put(created.getAccountNumber(), created);
        activeAccountNumber = created.getAccountNumber();

        System.out.print("Enter a nickname for this account (or press Enter to skip): ");
        String nick = keyboardInput.nextLine();
        created.setNickname(nick);

        System.out.print("Set a password for this account (min 4 characters): ");
        String pass = keyboardInput.nextLine();
        created.setPassword(pass);

        System.out.println("Additional account created: " + created.getDisplayName());
        System.out.println("This account is now active. Balance: $" + String.format("%.2f", created.getBalance()));
    }

    public void performSwitchActiveAccount() {
        List<BankAccount> selectableAccounts = getSelectableAccounts();

        if (selectableAccounts.isEmpty()) {
            System.out.println("There are no accounts available to switch to.");
            return;
        }

        System.out.println("Select an account to switch to and make active:");
        displayNumberedAccountList(selectableAccounts);

        int selection = promptAccountSelection(selectableAccounts.size());
        BankAccount selectedAccount = selectableAccounts.get(selection - 1);

        System.out.print("Enter password for " + selectedAccount.getDisplayName() + ": ");
        String passAttempt = keyboardInput.next();
        if (!selectedAccount.authenticate(passAttempt)) {
            System.out.println("Incorrect password. Switch cancelled.");
            return;
        }

        activeAccountNumber = selectedAccount.getAccountNumber();
        System.out.println("Active account switched to: " + selectedAccount.getDisplayName());
    }

    private List<BankAccount> getSelectableAccounts() {
        List<BankAccount> selectable = new ArrayList<>();
        for (BankAccount account : accountsByNumber.values()) {
            if (!account.getAccountNumber().equals(DUMMY_ACCOUNT_NUMBER)
                    && !account.isClosed()
                    && !account.getAccountNumber().equals(activeAccountNumber)) {
                selectable.add(account);
            }
        }
        return selectable;
    }

    private void displayNumberedAccountList(List<BankAccount> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i + 1) + ". " + accounts.get(i).getDisplayName());
        }
    }

    private int promptAccountSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Enter the number of the account you want to make active: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public void performCloseAccount() {
        BankAccount activeAccount = getActiveAccount();
        if (activeAccount.isFrozen()) {
            System.out.println("Unable to close account. Account is frozen; unlock it first.");
            return;
        }

        try {
            activeAccount.closeAccount();
            System.out.println("Closed Account " + activeAccount.getDisplayName());
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to close account. Account already closed");
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

    public void run() {
        performCreateAdditionalAccount(false);
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
}
