package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

public class BankAccountTest {

    @Test
    public void testDeposit() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(50);
        assertEquals(50, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidDeposit() {
        BankAccount testAccount = new BankAccount("TEST-1");
        try {
            testAccount.deposit(-50);
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testCollectFee() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(100);
        testAccount.collectFee(25);
        assertEquals(75, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testCollectFeeExceedingBalance() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(50);
        try {
            testAccount.collectFee(100);
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testCollectFeeNegativeAmount() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(50);
        try {
            testAccount.collectFee(-10);
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testCheckBalance() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(50);
        assertEquals(50, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testTransactionHistoryRecordsOperations() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(100);
        testAccount.collectFee(25);

        assertEquals(3, testAccount.getTransactionHistory().size());
        assertTrue(testAccount.getTransactionHistory().get(0).contains("Account opened"));
        assertTrue(testAccount.getTransactionHistory().get(1).contains("Deposit"));
        assertTrue(testAccount.getTransactionHistory().get(2).contains("Fee Collected"));
    }
}
