package test;

import main.BankAccount;
import main.BankAccount.AccountType;

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
    public void testSetAndGetNickname() {
        BankAccount account = new BankAccount("ACC-1001");
        account.setNickname("Savings");
        assertEquals("Savings", account.getNickname());
    }

    @Test
    public void testGetDisplayNameWithNickname() {
        BankAccount account = new BankAccount("ACC-1001");
        account.setNickname("Savings");
        assertEquals("Savings (ACC-1001) [Checking]", account.getDisplayName());
    }

    @Test
    public void testGetDisplayNameWithoutNickname() {
        BankAccount account = new BankAccount("ACC-1001");
        assertEquals("ACC-1001 [Checking]", account.getDisplayName());
    }

    @Test
    public void testNewAccountDefaultsToChecking() {
        BankAccount account = new BankAccount("X");
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertFalse(account.isSavings());
    }

    @Test
    public void testSavingsAccountType() {
        BankAccount account = new BankAccount("X", AccountType.SAVINGS);
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertTrue(account.isSavings());
    }

    @Test
    public void testSetNicknameBlankIsIgnored() {
        BankAccount account = new BankAccount("ACC-1001");
        account.setNickname("   ");
        assertEquals(null, account.getNickname());
    }

    @Test
    public void testSetNicknameTrimsWhitespace() {
        BankAccount account = new BankAccount("ACC-1001");
        account.setNickname("  Checking  ");
        assertEquals("Checking", account.getNickname());
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

    @Test
    public void testSetPasswordAndAuthenticate() {
        BankAccount account = new BankAccount("TEST-1");
        account.setPassword("secure1234");
        assertTrue(account.authenticate("secure1234"));
    }

    @Test
    public void testAuthenticateWithWrongPassword() {
        BankAccount account = new BankAccount("TEST-1");
        account.setPassword("secure1234");
        assertFalse(account.authenticate("wrongpass"));
    }

    @Test
    public void testAuthenticateWithNoPasswordSet() {
        BankAccount account = new BankAccount("TEST-1");
        assertTrue(account.authenticate("anything"));
    }

    @Test
    public void testSetPasswordRejectsNull() {
        BankAccount account = new BankAccount("TEST-1");
        try {
            account.setPassword(null);
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testSetPasswordRejectsBlank() {
        BankAccount account = new BankAccount("TEST-1");
        try {
            account.setPassword("   ");
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testSetPasswordRejectsTooShort() {
        BankAccount account = new BankAccount("TEST-1");
        try {
            account.setPassword("ab");
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing, test passes
        }
    }

    @Test
    public void testSetPasswordTrimsWhitespace() {
        BankAccount account = new BankAccount("TEST-1");
        account.setPassword("  mypass  ");
        assertTrue(account.authenticate("mypass"));
    }

    @Test
    public void testAuthenticateWithNullAttempt() {
        BankAccount account = new BankAccount("TEST-1");
        account.setPassword("secure1234");
        assertFalse(account.authenticate(null));
    }

    @Test
    public void testAuthenticateIsCaseSensitive() {
        BankAccount account = new BankAccount("TEST-1");
        account.setPassword("Pass123");
        assertFalse(account.authenticate("pass123"));
        assertFalse(account.authenticate("PASS123"));
        assertTrue(account.authenticate("Pass123"));
    }

    @Test
    public void testNewAccountNotBelowMinimum() {
        BankAccount account = new BankAccount("TEST-1");
        assertFalse(account.isBelowMinimumBalance());
    }

    @Test
    public void testIsBelowMinimumBalanceAfterMeetingThenDropping() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.withdraw(50);
        assertTrue(account.isBelowMinimumBalance());
    }

    @Test
    public void testIsBelowMinimumBalanceWhenAtMinimum() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        assertFalse(account.isBelowMinimumBalance());
    }

    @Test
    public void testIsBelowMinimumBalanceWhenAboveMinimum() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(150);
        assertFalse(account.isBelowMinimumBalance());
    }

    @Test
    public void testApplyMinimumBalanceFeeDeductsAmount() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.withdraw(50);
        account.applyMinimumBalanceFee();
        assertEquals(25.0, account.getBalance(), 0.01);
    }

    @Test
    public void testApplyMinimumBalanceFeeRecordedInHistory() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.withdraw(50);
        account.applyMinimumBalanceFee();
        boolean found = false;
        for (String entry : account.getTransactionHistory()) {
            if (entry.contains("Minimum Balance Fee")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testApplyMinimumBalanceFeeWhenBalanceLessThanFee() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.withdraw(90);
        account.applyMinimumBalanceFee();
        assertEquals(0.0, account.getBalance(), 0.01);
    }

    @Test
    public void testApplyMinimumBalanceFeeNotAppliedToNewAccount() {
        BankAccount account = new BankAccount("TEST-1");
        account.applyMinimumBalanceFee();
        assertEquals(0.0, account.getBalance(), 0.01);
    }

    @Test
    public void testApplyMinimumBalanceFeeOnClosedAccountThrows() {
        BankAccount account = new BankAccount("TEST-1");
        account.deposit(100);
        account.closeAccount();

        try {
            account.applyMinimumBalanceFee();
            fail();
        } catch (IllegalStateException e) {
            // do nothing, test passes
        }
    }

}

