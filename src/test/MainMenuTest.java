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
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        assertTrue(menu.getAdminLockoutEndTime() == 0);
    }

    @Test
    public void testAdminMenuLocksAfterThreeFailedAttempts() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\nwrong1\nwrong2\nwrong3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() > System.currentTimeMillis());
    }

    @Test
    public void testAdminMenuLockoutSetToTenSeconds() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\nwrong1\nwrong2\nwrong3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() <= System.currentTimeMillis() + 10000);
    }

    @Test
    public void testAdminMenuNotLockedAfterSuccessfulLogin() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\nadmin123\n8\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() == 0);
    }

    @Test
    public void testDepositConfirmedUpdatesBalance() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\n50\nyes\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        assertEquals(50.05, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testDepositCancelledLeavesBalanceUnchanged() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\n50\nno\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        assertEquals(0.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testWithdrawalConfirmedUpdatesBalance() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\n50\nyes\n20\nyes\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        menu.performWithdrawal();
        assertEquals(30.05, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testWithdrawalCancelledLeavesBalanceUnchanged() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\n50\nyes\n20\nno\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();
        menu.performWithdrawal();
        assertEquals(50.05, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testSwitchActiveAccountValid() {
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("1\nChecking\npass1234\n" +  // type=Checking, nickname, password for first account
            "100\nyes\n" +                // deposit into first account
            "2\nSavings\npass5678\n" +    // type=Savings, nickname, password for second account
            "200\nyes\n" +                // deposit into second account
            "1\npass1234\n").getBytes()   // switch back to first account + password
        ));

        MainMenu menu = new MainMenu(scanner);

        menu.performCreateAdditionalAccount(false);
        menu.performDeposit();

        menu.performCreateAdditionalAccount(true);
        menu.performDeposit();

        menu.performSwitchActiveAccount();

        assertEquals(100.1, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testSwitchAccountsWithNoneAvailable() {
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("1\nChecking\npass1234\n").getBytes()
        ));

        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);

        String beforeAccount = menu.getActiveAccountNumber();

        menu.performSwitchActiveAccount();

        String afterAccount = menu.getActiveAccountNumber();

        assertEquals(beforeAccount, afterAccount);
    }

    @Test
    public void testCreateAccountSelectsChecking() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n\npass1234\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        assertEquals(0.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testCreateAccountSelectsSavings() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("2\n\npass1234\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);
        assertEquals(0.0, menu.getActiveAccountBalance(), 0.01);
    }

    @Test
    public void testAccountCreationSetsPassword() {
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("1\nMyAccount\nsecure99\n").getBytes()
        ));

        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount(false);

        // account was created without error, password was accepted
        assertEquals("ACC-1001", menu.getActiveAccountNumber());
    }

    @Test
    public void testSwitchAccountWithCorrectPassword() {
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("1\nFirst\npass1234\n" +     // type=Checking, nickname, password for first account
            "1\nSecond\npass5678\n" +      // type=Checking, nickname, password for second account
            "1\npass1234\n").getBytes()    // switch to first account + correct password
        ));

        MainMenu menu = new MainMenu(scanner);

        menu.performCreateAdditionalAccount(false);
        menu.performCreateAdditionalAccount(false);

        menu.performSwitchActiveAccount();

        assertEquals("ACC-1001", menu.getActiveAccountNumber());
    }

    @Test
    public void testSwitchAccountWithIncorrectPassword() {
        Scanner scanner = new Scanner(new ByteArrayInputStream(
            ("1\nFirst\npass1234\n" +     // type=Checking, nickname, password for first account
            "1\nSecond\npass5678\n" +      // type=Checking, nickname, password for second account
            "1\nwrongpass\n").getBytes()   // switch to first account + wrong password
        ));

        MainMenu menu = new MainMenu(scanner);

        menu.performCreateAdditionalAccount(false);
        menu.performCreateAdditionalAccount(false);

        String beforeAccount = menu.getActiveAccountNumber();

        menu.performSwitchActiveAccount();

        // active account should NOT have changed
        assertEquals(beforeAccount, menu.getActiveAccountNumber());
    }

}
