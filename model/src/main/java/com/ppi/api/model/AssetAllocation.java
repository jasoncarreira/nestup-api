package com.ppi.api.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.IOException;
import java.io.Serializable;

@Embeddable
public class AssetAllocation implements Serializable, DataSerializable {
    @Column(name = "cash_pct")
    private double cashPercentage;
    @Column(name = "bond_pct")
    private double bondPercentage;
    @Column(name = "stock_pct")
    private double stockPercentage;

    public AssetAllocation() {
    }

    public AssetAllocation(double cashPercentage, double bondPercentage, double stockPercentage) {
        this.cashPercentage = cashPercentage;
        this.bondPercentage = bondPercentage;
        this.stockPercentage = stockPercentage;
    }

    public double getCashPercentage() {
        return cashPercentage;
    }

    public void setCashPercentage(double cashPercentage) {
        this.cashPercentage = cashPercentage;
    }

    public double getBondPercentage() {
        return bondPercentage;
    }

    public void setBondPercentage(double bondPercentage) {
        this.bondPercentage = bondPercentage;
    }

    public double getStockPercentage() {
        return stockPercentage;
    }

    public void setStockPercentage(double stockPercentage) {
        this.stockPercentage = stockPercentage;
    }

    public AssetAllocation copyFrom(AssetAllocation other) {
        this.cashPercentage = other.cashPercentage;
        this.bondPercentage = other.bondPercentage;
        this.stockPercentage = other.stockPercentage;
        return this;
    }

    @Override
    public String toString() {
        return "AssetAllocation{" +
                "cashPercentage=" + cashPercentage +
                ", bondPercentage=" + bondPercentage +
                ", stockPercentage=" + stockPercentage +
                '}';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeDouble(cashPercentage);
        out.writeDouble(bondPercentage);
        out.writeDouble(stockPercentage);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.cashPercentage = in.readDouble();
        this.bondPercentage = in.readDouble();
        this.stockPercentage = in.readDouble();
    }
}
