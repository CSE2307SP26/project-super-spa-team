package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.security.SecureRandom;

public class BankAccount {

    public enum AccountType {
        CHECKING, SAVINGS
    }

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] UNLOCK_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final double MAX_TRANSACTION_LIMIT = 5000.0;

    static final double MINIMUM_BALANCE = 100.0;
    static final double MINIMUM_BALANCE_FEE = 25.0;

    private final String accountNumber;
    private final AccountType accountType;
    private double balance;
    private final List<String> transactionHistory;
    private boolean closed;
    private String nickname;
    private boolean frozen;
    private String code;
    private boolean hasMetMinimumBalance;

    public BankAccount(String accountNumber) {
        this(accountNumber, AccountType.CHECKING);
    }

    public BankAccount(String accountNumber, AccountType accountType) {
        String id = Objects.requireNonNull(accountNumber, "accountNumber").trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("accountNumber must not be blank");
        }
        this.accountNumber = id;
        this.accountType = Objects.requireNonNull(accountType, "accountType");
        this.balance = 0;
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add("Account opened: " + this.accountNumber);
        this.closed = false;
        this.frozen = false;
        this.code = null;
        this.hasMetMinimumBalance = false;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public boolean isSavings() {
        return accountType == AccountType.SAVINGS;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = (nickname == null || nickname.isBlank()) ? null : nickname.trim();
    }

    public String getDisplayName() {
        String typeLabel = "[" + accountType.name().charAt(0)
            + accountType.name().substring(1).toLowerCase() + "]";
        if (nickname == null) {
            return accountNumber + " " + typeLabel;
        }
        return nickname + " (" + accountNumber + ") " + typeLabel;
    }

    public double getBalance() {
        return this.balance;
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

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(this.transactionHistory);
    }

    public boolean isBelowMinimumBalance() {
        return hasMetMinimumBalance && this.balance < MINIMUM_BALANCE;
    }

    public void deposit(double amount) {
        if (amount <= 0 || amount > MAX_TRANSACTION_LIMIT) {
            throw new IllegalArgumentException();
        }
        this.balance += amount;
        this.transactionHistory.add("Deposit: $" + String.format("%.2f", amount));
        if (this.balance >= MINIMUM_BALANCE) {
            this.hasMetMinimumBalance = true;
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0 || amount > this.balance || amount > MAX_TRANSACTION_LIMIT) {
            throw new IllegalArgumentException();
        }
        this.balance -= amount;
        this.transactionHistory.add("Withdrawal: $" + String.format("%.2f", amount));
    }

    public void collectFee(double fee) {
        if (fee <= 0 || fee > this.balance) {
            throw new IllegalArgumentException();
        }
        this.balance -= fee;
        this.transactionHistory.add("Fee Collected: $" + String.format("%.2f", fee));
    }

    public void addInterest(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        }
        this.balance += amount;
        this.transactionHistory.add("Interest Payment: $" + String.format("%.2f", amount));
        if (this.balance >= MINIMUM_BALANCE) {
            this.hasMetMinimumBalance = true;
        }
    }

    public void transfer(BankAccount recipient, double amount) {
        if (recipient == null) {
            throw new IllegalArgumentException();
        }
        if (amount <= 0 || amount > MAX_TRANSACTION_LIMIT) {
            throw new IllegalArgumentException();
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException();
        }
        this.balance -= amount;
        recipient.balance += amount;
    }

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

    public void applyMinimumBalanceFee() {
        if (this.closed) {
            throw new IllegalStateException("Cannot apply fee to a closed account.");
        }
        if (!isBelowMinimumBalance()) {
            return;
        }
        double fee = Math.min(MINIMUM_BALANCE_FEE, this.balance);
        this.balance -= fee;
        this.transactionHistory.add("Minimum Balance Fee: $" + String.format("%.2f", MINIMUM_BALANCE_FEE));
    }

    public void closeAccount() {
        if (this.closed) {
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
