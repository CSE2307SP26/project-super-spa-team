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
    private static final double maxTransactionLimit = 5000.0;
    private String nickname;
    private boolean frozen;
    private String code;
    private String password;

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
        this.password = null;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = (nickname == null || nickname.isBlank()) ? null : nickname.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }
        String trimmed = password.trim();
        if (trimmed.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        this.password = trimmed;
    }

    public boolean authenticate(String attempt) {
        if (this.password == null) {
            return true;
        }
        if (attempt == null || attempt.trim().isEmpty()) {
            return false;
        }
        return this.password.equals(attempt.trim());
    }

    public String getDisplayName() {
        if (nickname == null) return accountNumber;
        return nickname + " (" + accountNumber + ")";
    }

    public void deposit(double amount) {
        if (amount > 0 && amount <= maxTransactionLimit) {
            this.balance += amount;
            this.transactionHistory.add("Deposit: $" + String.format("%.2f", amount));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= this.balance && amount <= maxTransactionLimit) {
            this.balance -= amount;
            this.transactionHistory.add("Withdrawal: $" + String.format("%.2f", amount));
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
            this.transactionHistory.add("Fee Collected: $" + String.format("%.2f", fee));
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
            this.transactionHistory.add("Interest Payment: $" + String.format("%.2f", amount));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void transfer(BankAccount recipient, double amount) {
        if (recipient == null) {
            throw new IllegalArgumentException();
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException();
        }
        if (amount <= 0 || amount > maxTransactionLimit) {
            throw new IllegalArgumentException();
        }
        this.balance -= amount;
        recipient.balance += amount;
    }

    public boolean isClosed() {
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
