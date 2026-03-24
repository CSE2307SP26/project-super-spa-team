import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new BankApp().run();
    }
}

class BankApp {
    private static final int EXIT_OPTION = 10;

    private final Scanner scanner;
    private final Map<String, Account> accounts;

    BankApp() {
        this.scanner = new Scanner(System.in);
        this.accounts = new LinkedHashMap<>();
        testData();
    }

    void run() {
        int selection = -1;
        while (selection != EXIT_OPTION) {
            printMenu();
            selection = readMenuSelection();
            handleSelection(selection);
            System.out.println();
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("Hello, welcome to the bank!");
        System.out.println("How can we help you today?");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Check Balance");
        System.out.println("4. View Transaction History");
        System.out.println("5. Create Account");
        System.out.println("6. Close Account");
        System.out.println("7. Transfer Money");
        System.out.println("8. Collect Fees");
        System.out.println("9. Add Interest");
        System.out.println("10. Exit");
    }

    private int readMenuSelection() {
        while (true) {
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= 1 && value <= EXIT_OPTION) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid selection. Please enter a number from 1 to 10.");
            }
        }
    }

    private void handleSelection(int selection) {
        switch (selection) {
            case 4:
                viewTransactionHistory();
                break;
            case EXIT_OPTION:
                System.out.println("Goodbye!");
                break;
            default:
                System.out.println("This feature is not implemented yet! Please try again.");
        }
    }

    // Task 4: A bank customer should be able to view their transaction history for an account.
    private void viewTransactionHistory() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim();
        Account account = accounts.get(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        List<String> history = account.getTransactionHistory();
        System.out.println("Transaction history for account " + accountNumber + ":");

        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }
    // We can test the feature with these sample accounts
    private void testData() {
        Account checking = new Account("CHK1001", 1200.00);
        checking.recordTransaction("Account opened with balance $1200.00");
        checking.recordTransaction("Deposit $150.00");
        checking.recordTransaction("Withdrawal $35.00");
        accounts.put(checking.getAccountNumber(), checking);

        Account savings = new Account("SAV2001", 5000.00);
        savings.recordTransaction("Account opened with balance $5000.00");
        savings.recordTransaction("Interest payment $10.00");
        accounts.put(savings.getAccountNumber(), savings);
    }
}

class Account {
    private final String accountNumber;
    private double balance;
    private final List<String> transactionHistory;

    Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
    }

    String getAccountNumber() {
        return accountNumber;
    }

    double getBalance() {
        return balance;
    }

    List<String> getTransactionHistory() {
        return transactionHistory;
    }

    void recordTransaction(String transaction) {
        transactionHistory.add(transaction);
    }
}
