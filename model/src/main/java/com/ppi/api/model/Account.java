package com.ppi.api.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Account
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "accounts")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Account extends BaseEntity {
    private String name;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    private double balance;
    private double contribution;
    @Column(name = "cash_pct")
    private int cashPercentage;
    @Column(name = "bond_pct")
    private int bondPercentage;
    @Column(name = "stock_pct")
    private int stockPercentage;
    @Column(name = "owner_id", insertable = false, updatable = false)
    private String ownerId;
    @ManyToOne
    @XmlTransient
    @JoinColumn(name = "owner_id")
    private NestupUser owner;

    public Account() {
        super(null);
    }

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

    public int getCashPercentage() {
        return cashPercentage;
    }

    public void setCashPercentage(int cashPct) {
        this.cashPercentage = cashPct;
    }

    public int getBondPercentage() {
        return bondPercentage;
    }

    public void setBondPercentage(int bondPct) {
        this.bondPercentage = bondPct;
    }

    public int getStockPercentage() {
        return stockPercentage;
    }

    public void setStockPercentage(int stockPct) {
        this.stockPercentage = stockPct;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public NestupUser getOwner() {
        return owner;
    }

    public void setOwner(NestupUser owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                ", contribution=" + contribution +
                ", cashPct=" + cashPercentage +
                ", bondPct=" + bondPercentage +
                ", stockPct=" + stockPercentage +
                '}';
    }
}
