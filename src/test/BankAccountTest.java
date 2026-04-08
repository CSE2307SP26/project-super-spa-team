package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @Test
    public void testDepositingOverMaxLimit(){
        BankAccount account = new BankAccount("1");

        try{
            account.deposit(5001);
            fail(); // depositng 5001 should be illegal
        } catch (IllegalArgumentException e){
        //do nothing, test passes
        }
    }

    @Test
    public void testWithdrawingOverMaxLimit(){
        BankAccount account = new BankAccount("1");
        account.deposit(4000);
        account.deposit(4000);

        try{
            account.withdraw(5001);
            fail(); // withdrawing 5001 should be illegal
        } catch (IllegalArgumentException e){
        //do nothing, test passes
        }
    }

    @Test
    public void testTransferringOverMaxLimit(){
        BankAccount source = new BankAccount("1");
        BankAccount recipient = new BankAccount("2");
        
        source.deposit(4000);
        source.deposit(4000);

        try{
            source.transfer(recipient, 5001);
            fail(); // transferring 5001 should be illegal
        } catch (IllegalArgumentException e){
        //do nothing, test passes
        }
    }

    @Test
    public void testFreezeAndIsFrozen() {
        BankAccount account = new BankAccount("1");
        assertFalse(account.isFrozen());
        
        String code = account.freeze();
        
        assertTrue(account.isFrozen());
        assertTrue(code != null && code.length() == 6);
        
        // freezing again should return the same code
        String code2 = account.freeze();
        assertEquals(code, code2);
    }

    @Test
    public void testUnlockWithCorrectCode() {
        BankAccount account = new BankAccount("1");
        String code = account.freeze();
        
        boolean result = account.unlock(code);
        
        assertTrue(result);
        assertFalse(account.isFrozen());
    }

    @Test
    public void testUnlockWithIncorrectCode() {
        BankAccount account = new BankAccount("1");
        account.freeze();
        
        boolean result = account.unlock("WRONG!");
        
        assertFalse(result);
        assertTrue(account.isFrozen());
    }

    @Test
    public void testUnlockWithNullOrEmptyCode() {
        BankAccount account = new BankAccount("1");
        account.freeze();
        
        assertFalse(account.unlock(null));
        assertFalse(account.unlock(""));
        assertFalse(account.unlock("   "));
        assertTrue(account.isFrozen());
    }

    @Test
    public void testFreezeOnClosedAccountThrows() {
        BankAccount account = new BankAccount("1");
        account.closeAccount();
        
        try {
            account.freeze();
            fail();
        } catch (IllegalStateException e) {
            // pass
        }
    }

}
