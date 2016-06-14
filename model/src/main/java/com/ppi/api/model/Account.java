package com.ppi.api.model;

import javax.persistence.*;

/**
 * Account
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {
    private String name;
    private AccountType type;
    private double balance;
    private double contribution;
    @Column(name = "cash_pct")
    private int cashPct;
    @Column(name = "bond_pct")
    private int bondPct;
    @Column(name = "stock_pct")
    private int stockPct;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getContribution() {
        return contribution;
    }

    public void setContribution(double contribution) {
        this.contribution = contribution;
    }

    public int getCashPct() {
        return cashPct;
    }

    public void setCashPct(int cashPct) {
        this.cashPct = cashPct;
    }

    public int getBondPct() {
        return bondPct;
    }

    public void setBondPct(int bondPct) {
        this.bondPct = bondPct;
    }

    public int getStockPct() {
        return stockPct;
    }

    public void setStockPct(int stockPct) {
        this.stockPct = stockPct;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                ", contribution=" + contribution +
                ", cashPct=" + cashPct +
                ", bondPct=" + bondPct +
                ", stockPct=" + stockPct +
                '}';
    }
}
