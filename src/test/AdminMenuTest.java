package test;

import main.AdminMenu;
import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class AdminMenuTest {

    @Test
    public void testAuthenticateSuccessOnFirstAttempt() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("admin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateSuccessOnSecondAttempt() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrongpass\nadmin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateSuccessOnThirdAttempt() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrong1\nwrong2\nadmin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateFailureAfterThreeWrongAttempts() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrong1\nwrong2\nwrong3\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());
        assertFalse(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateIsCaseSensitive() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("Admin123\nADMIN123\nadmin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testApplyMinimumBalanceFeesChargesBelowMinimumAccount() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.withdraw(50);

        Map<String, BankAccount> accounts = new HashMap<>();
        accounts.put(account.getAccountNumber(), account);

        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, accounts);
        adminMenu.performApplyMinimumBalanceFees();

        assertEquals(25.0, account.getBalance(), 0.01);
    }

    @Test
    public void testApplyMinimumBalanceFeesSkipsAccountAboveMinimum() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(150);

        Map<String, BankAccount> accounts = new HashMap<>();
        accounts.put(account.getAccountNumber(), account);

        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, accounts);
        adminMenu.performApplyMinimumBalanceFees();

        assertEquals(150.0, account.getBalance(), 0.01);
    }

    @Test
    public void testApplyMinimumBalanceFeesSkipsClosedAccount() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.withdraw(50);
        account.closeAccount();

        Map<String, BankAccount> accounts = new HashMap<>();
        accounts.put(account.getAccountNumber(), account);

        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, accounts);
        adminMenu.performApplyMinimumBalanceFees();
        assertEquals(50.0, account.getBalance(), 0.01);
    }

    @Test
    public void testViewTransactionHistoryShowsTransactions() {
        BankAccount account = new BankAccount("TEST-001");
        account.deposit(100.00);
        account.withdraw(25.00);
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        adminMenu.performViewTransactionHistory();
        System.setOut(System.out);

        String output = out.toString();
        assertTrue(output.contains("Account opened: TEST-001"));
        assertTrue(output.contains("Deposit: $100.00"));
        assertTrue(output.contains("Withdrawal: $25.00"));
    }

    @Test
    public void testViewTransactionHistoryShowsAccountOpened() {
        BankAccount account = new BankAccount("TEST-002");
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner, new java.util.HashMap<>());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        adminMenu.performViewTransactionHistory();
        System.setOut(System.out);

        String output = out.toString();
        assertTrue(output.contains("Account opened: TEST-002"));
    }

}

