package com.ppi.api.model;

import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.PredicateBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Portfolio
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "portfolios")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Portfolio extends BaseEntity<Portfolio> implements UserOwned {
    private String name;
    @Enumerated(EnumType.STRING)
    private PortfolioType type;
    private double balance;
    private double contribution;
    @Embedded
    private AssetAllocation assetAllocation;
    @Column(name = "owner_id", insertable = false, updatable = false)
    private String ownerId;

    @ManyToOne
    @XmlTransient
    @JoinColumn(name = "owner_id")
    private Participant owner;

    @Transient
    @XmlTransient
    private Set<PortfolioInstrument> instruments;

    public Portfolio() {
        super(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PortfolioType getType() {
        return type;
    }

    public void setType(PortfolioType type) {
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

    public AssetAllocation getAssetAllocation() {
        return assetAllocation;
    }

    public void setAssetAllocation(AssetAllocation assetAllocation) {
        this.assetAllocation = assetAllocation;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    public Participant getOwner() {
        if (owner == null && ownerId != null && hazelcastInstance != null) {
            owner = (Participant) hazelcastInstance.getMap(Participant.MAP_NAME).get(ownerId);
            if (owner != null) {
                owner.setHazelcastInstance(this.hazelcastInstance);
            }
        }
        return owner;
    }

    public void setOwner(Participant owner) {
        this.owner = owner;
        this.ownerId = owner.getId();
    }

    public Set<PortfolioInstrument> getInstruments() {
        if (instruments == null && hazelcastInstance != null) {
            IMap<String, PortfolioInstrument> portfolioInstrumentsMap = hazelcastInstance.getMap(PortfolioInstrument.MAP_NAME);
            EntryObject e = new PredicateBuilder().getEntryObject();
            PredicateBuilder predicate = e.get("portfolioId").equal(id);
            Collection<PortfolioInstrument> values = portfolioInstrumentsMap.values(predicate);
            instruments = new HashSet<>(values.size());
            for (PortfolioInstrument next : values) {
                next.setHazelcastInstance(hazelcastInstance);
                next.setPortfolio(this);
                instruments.add(next);
            }
        }
        return instruments == null ? instruments = new HashSet<>() : instruments;
    }

    public void addInstrument(PortfolioInstrument portfolioInstrument) {
        Set<PortfolioInstrument> instruments = getInstruments();
        portfolioInstrument.setPortfolio(this);
        portfolioInstrument.setParticipantId(ownerId);
        instruments.add(portfolioInstrument);
        reCalculate();
    }

    public void reCalculate() {
        Set<PortfolioInstrument> instruments = getInstruments();
        if (instruments.size() > 0) {

            double totalBalance = 0;
            double totalCashBalance = 0;
            double totalBondBalance = 0;
            double totalStockBalance = 0;
            double totalContribution = 0;
            for (PortfolioInstrument pi : instruments) {
                double balance = pi.getBalance();
                double contribution = pi.getContribution();
                AssetAllocation assetAllocation = pi.getInstrument().getAssetAllocation();
                double cashBalance = balance * assetAllocation.getCashPercentage() / 100;
                double bondBalance = balance * assetAllocation.getBondPercentage() / 100;
                double stockBalance = balance * assetAllocation.getStockPercentage() / 100;
                totalBalance += balance;
                totalCashBalance += cashBalance;
                totalBondBalance += bondBalance;
                totalStockBalance += stockBalance;
                totalContribution += contribution;
            }
            setContribution(totalContribution);
            setBalance(totalBalance);
            setAssetAllocation(new AssetAllocation(totalCashBalance/totalBalance,totalBondBalance/totalBalance,totalStockBalance/totalBalance));
        }
    }

    public void setInstruments(Set<PortfolioInstrument> instruments) {
        this.instruments = instruments;
    }

    @Override
    public Portfolio copyFrom(Portfolio other) {
        this.name = other.name;
        this.type = other.type;
        this.balance = other.balance;
        this.contribution = other.contribution;
        this.assetAllocation = new AssetAllocation().copyFrom(other.assetAllocation);
        this.ownerId = other.ownerId;
        return this;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                ", contribution=" + contribution +
                ", assetAllocation=" + assetAllocation +
                ", ownerId='" + ownerId + '\'' +
                "} " + super.toString();
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(name);
        out.writeObject(type);
        out.writeDouble(balance);
        out.writeDouble(contribution);
        out.writeObject(assetAllocation);
        out.writeUTF(ownerId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.name = in.readUTF();
        this.type = in.readObject();
        this.balance = in.readDouble();
        this.contribution = in.readDouble();
        this.assetAllocation = in.readObject();
        this.ownerId = in.readUTF();
    }
}
