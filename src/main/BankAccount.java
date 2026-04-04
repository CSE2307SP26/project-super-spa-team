package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BankAccount {

    private final String accountNumber;
    private double balance;
    private final List<String> transactionHistory;
    private boolean closed;

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

    public void closeAccount(){
        if (this.closed){
            throw new IllegalArgumentException();
        }
        this.closed = true;
        this.transactionHistory.add("Closed Account: " + this.accountNumber);
    }
}
