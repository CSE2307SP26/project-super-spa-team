package test;

import main.AdminMenu;
import main.BankAccount;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class AdminMenuTest {

    @Test
    public void testAuthenticateSuccessOnFirstAttempt() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("admin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner);
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateSuccessOnSecondAttempt() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrongpass\nadmin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner);
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateSuccessOnThirdAttempt() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrong1\nwrong2\nadmin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner);
        assertTrue(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateFailureAfterThreeWrongAttempts() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrong1\nwrong2\nwrong3\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner);
        assertFalse(adminMenu.authenticate());
    }

    @Test
    public void testAuthenticateIsCaseSensitive() {
        BankAccount account = new BankAccount("TEST-ADMIN");
        Scanner scanner = new Scanner(new ByteArrayInputStream("Admin123\nADMIN123\nadmin123\n".getBytes()));
        AdminMenu adminMenu = new AdminMenu(account, scanner);
        assertTrue(adminMenu.authenticate());
    }

}
