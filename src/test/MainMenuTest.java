package test;

import main.MainMenu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class MainMenuTest {

    @Test
    public void testAdminMenuNotLockedInitially() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        assertTrue(menu.getAdminLockoutEndTime() == 0);
    }

    @Test
    public void testAdminMenuLocksAfterThreeFailedAttempts() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\nwrong1\nwrong2\nwrong3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() > System.currentTimeMillis());
    }

    @Test
    public void testAdminMenuLockoutSetToTenSeconds() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\nwrong1\nwrong2\nwrong3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() <= System.currentTimeMillis() + 10000);
    }

    @Test
    public void testAdminMenuNotLockedAfterSuccessfulLogin() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\nadmin123\n7\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() == 0);
    }

    @Test
    public void testDepositConfirmedUpdatesBalance() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n50\nyes\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        assertEquals(50.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testDepositCancelledLeavesBalanceUnchanged() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n50\nno\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        assertEquals(0.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testWithdrawalConfirmedUpdatesBalance() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n50\nyes\n20\nyes\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        menu.performWithdrawal();
        assertEquals(30.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testWithdrawalCancelledLeavesBalanceUnchanged() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n50\nyes\n20\nno\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        menu.performWithdrawal();
        assertEquals(50.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testSwitchActiveAccountValid() {
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("Checking\n" +     // nickname for first account
            "100\nyes\n" +     // deposit into first account
            "Savings\n" +    // nickname for second account
            "200\nyes\n" +     // deposit into second account
            "1\n").getBytes()  // switch back to first account
        ));

        MainMenu menu = new MainMenu(scanner);

        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();

        menu.performCreateAdditionalAccount(true);
        menu.performDeposit();

        menu.performSwitchActiveAccount();

        assertEquals(100.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testSwitchAccountsWithNoneAvailable() {
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("Checking\n").getBytes()
        ));

        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);

        String beforeAccount = menu.getActiveAccountNumber();

        menu.performSwitchActiveAccount();

        String afterAccount = menu.getActiveAccountNumber();

        assertEquals(beforeAccount, afterAccount);
    }

}
