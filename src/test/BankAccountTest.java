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
    public void testWithdraw() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(50);
        testAccount.withdraw(20);
        assertEquals(30, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidWithdraw() {
        BankAccount testAccount = new BankAccount("TEST-1");
        try {
            testAccount.withdraw(-50);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
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
    public void testInsufficientFunds() {
        BankAccount testAccount = new BankAccount("TEST-1");
        try {
            testAccount.withdraw(50);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
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
    public void testAddInterest() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(100);
        testAccount.addInterest(50);
        assertEquals(150, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testAddInterestNegativeAmount() {
        BankAccount testAccount = new BankAccount("TEST-1");
        testAccount.deposit(100);
        try {
            testAccount.addInterest(-50);
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
        testAccount.addInterest(50);
        testAccount.withdraw(50);

        assertEquals(5, testAccount.getTransactionHistory().size());
        assertTrue(testAccount.getTransactionHistory().get(0).contains("Account opened"));
        assertTrue(testAccount.getTransactionHistory().get(1).contains("Deposit"));
        assertTrue(testAccount.getTransactionHistory().get(2).contains("Fee Collected"));
        assertTrue(testAccount.getTransactionHistory().get(3).contains("Interest Payment"));
        assertTrue(testAccount.getTransactionHistory().get(4).contains("Withdrawal"));
    }

    @Test
    public void testAllowedTransfer(){
        BankAccount source = new BankAccount("1");
        BankAccount recipient = new BankAccount("2");

        source.deposit(100);
        source.transfer(recipient, 25);

        assertEquals(75, source.getBalance(), 0.01);
        assertEquals(25, recipient.getBalance(), 0.01);
    }

    @Test
    public void testTransferringNegativeAmount(){
        BankAccount source = new BankAccount("1");
        BankAccount recipient = new BankAccount("2");

        source.deposit(100);

        try{
            source.transfer(recipient, -25);
            fail(); 
        } catch(IllegalArgumentException e){
            //do nothing, test passes
        }
    }

    @Test
    public void testTransferringZeroAmount(){
        BankAccount source = new BankAccount("1");
        BankAccount recipient = new BankAccount("2");

        source.deposit(100);

        try {
            source.transfer(recipient, 0);
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testTransferringMoreThanBalance(){
        BankAccount source = new BankAccount("1");
        BankAccount recipient = new BankAccount("2");

        source.deposit(100);

        try{
            source.transfer(recipient, 110);
            fail();
        } catch( IllegalArgumentException e){
            // do nothing, test passes
        }
    }

    @Test
    public void testTransferringAllBalance(){
        BankAccount source = new BankAccount("1");
        BankAccount recipient = new BankAccount("2");

        source.deposit(100);
        recipient.deposit(10);

        source.transfer(recipient, 100);

        assertEquals(0, source.getBalance(), 0.01);
        assertEquals(110, recipient.getBalance(), 0.01);
    }

    @Test
    public void testClosingAccount(){
        BankAccount account = new BankAccount("1");
        account.deposit(50);
        account.closeAccount();
        assertTrue(account.isClosed());
    }

    @Test
    public void testClosingAlreadyClosedAccount(){
        BankAccount account = new BankAccount("1");
        account.deposit(50);
        account.closeAccount();

        try{
            account.closeAccount();
            fail(); // shouldn't be able to get here
        }catch (IllegalArgumentException e){
            //do nothing,test passes
        }
    }


}
