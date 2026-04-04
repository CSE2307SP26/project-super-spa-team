package test;

import main.MainMenu;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class MainMenuTest {

    @Test
    public void testAdminMenuNotLockedInitially() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount();
        assertTrue(menu.getAdminLockoutEndTime() == 0);
    }

    @Test
    public void testAdminMenuLocksAfterThreeFailedAttempts() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrong1\nwrong2\nwrong3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount();
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() > System.currentTimeMillis());
    }

    @Test
    public void testAdminMenuLockoutSetToTenSeconds() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("wrong1\nwrong2\nwrong3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount();
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() <= System.currentTimeMillis() + 10000);
    }

    @Test
    public void testAdminMenuNotLockedAfterSuccessfulLogin() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("admin123\n3\n".getBytes()));
        MainMenu menu = new MainMenu(scanner);
        menu.performCreateAdditionalAccount();
        menu.performAdminMenu();
        assertTrue(menu.getAdminLockoutEndTime() == 0);
    }

}
