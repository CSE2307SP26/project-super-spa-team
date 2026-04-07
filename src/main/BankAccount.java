package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.security.SecureRandom;

public class BankAccount {

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] UNLOCK_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final String accountNumber;
    private double balance;
    private final List<String> transactionHistory;
    private boolean closed;
    private boolean frozen;
    private String code;

    public BankAccount(String accountNumber) {
        String id = Objects.requireNonNull(accountNumber, "accountNumber").trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("accountNumber must not be blank");
        }
        this.accountNumber = id;
        this.balance = 0;
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add("Account opened: " + this.accountNumber);
        this.closed = false;
        this.frozen = false;
        this.code = null;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
            this.transactionHistory.add("Deposit: $" + amount);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= this.balance){
            this.balance -= amount;
            this.transactionHistory.add("Withdrawal: $" + amount);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public void collectFee(double fee) {
        if (fee > 0 && fee <= this.balance) {
            this.balance -= fee;
            this.transactionHistory.add("Fee Collected: $" + fee);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(this.transactionHistory);
    }
  
    public void addInterest(double amount) {
        if (amount > 0) {
            this.balance += amount;
            this.transactionHistory.add("Interest Payment: $" + amount);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void transfer(BankAccount recipient, double amount){
        if (recipient == null){
            throw new IllegalArgumentException();
        }
        if(amount > this.balance){
            throw new IllegalArgumentException();
        }
        if (amount <= 0){
            throw new IllegalArgumentException();
        }
        this.balance -= amount;
        recipient.balance += amount;
    }

    public boolean isClosed(){
        return this.closed;
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * Freezes the account and creates an unlock code. Returns the generated code.
     * Code is optional and will remain null unless an account has been frozen.
     */
    public String freeze() {
        if (this.closed) {
            throw new IllegalStateException("Cannot freeze a closed account.");
        }
        if (this.frozen) {
            return this.code;
        }

        this.frozen = true;
        this.code = generateUnlockCode(6);
        this.transactionHistory.add("Account frozen: " + this.accountNumber);
        return this.code;
    }

    public boolean unlock(String codeAttempt) {
        if (!this.frozen) {
            return true;
        }
        if (codeAttempt == null) {
            return false;
        }

        String attempt = codeAttempt.trim();
        if (attempt.isEmpty()) {
            return false;
        }
        if (this.code == null) {
            return false;
        }
        if (!this.code.equals(attempt)) {
            return false;
        }

        this.frozen = false;
        this.code = null;
        this.transactionHistory.add("Account unlocked: " + this.accountNumber);
        return true;
    }

    public void closeAccount(){
        if (this.closed){
            throw new IllegalArgumentException();
        }
        this.closed = true;
        this.transactionHistory.add("Closed Account: " + this.accountNumber);
    }

    private static String generateUnlockCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(UNLOCK_CODE_ALPHABET[RNG.nextInt(UNLOCK_CODE_ALPHABET.length)]);
        }
        return sb.toString();
    }
}
