package com.ppi.api.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

@Entity
@Table(name = "portfolio_instruments", indexes = {@Index(columnList = "portfolio_id,instrument_id", unique = true)})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PortfolioInstrument extends BaseEntity<PortfolioInstrument> implements UserOwned {
    public static final String MAP_NAME = "portfolioinstruments";
    @Column(name = "portfolio_id")
    private String portfolioId;

    @Transient
    @XmlTransient
    private Portfolio portfolio;

    @Column(name = "instrument_id")
    private String instrumentId;

    @Transient
    @XmlTransient
    private Instrument instrument;

    @XmlTransient
    private String participantId;

    private double balance;
    private double contribution;


    public PortfolioInstrument() {
        super(MAP_NAME);
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        if (portfolio != null) this.portfolioId = portfolio.getId();
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrument_id) {
        this.instrumentId = instrument_id;
    }

    public Instrument getInstrument() {
        if (instrument == null && instrumentId != null && hazelcastInstance != null) {
            instrument = (Instrument) hazelcastInstance.getMap(Instrument.MAP_NAME).get(instrumentId);
            if (instrument != null) instrument.setHazelcastInstance(hazelcastInstance);
        }
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
        this.instrumentId = instrument.getId();
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

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    @Override
    public String getOwnerId() {
        return participantId;
    }

    /**
     * Copy the fields from the other instance. Should return "this" to enable fluent API
     *
     * @param other other instance to copy from
     * @return this
     */
    @Override
    public PortfolioInstrument copyFrom(PortfolioInstrument other) {
        this.participantId = other.participantId;
        this.portfolioId = other.portfolioId;
        this.instrumentId = other.instrumentId;
        this.balance = other.balance;
        this.contribution = other.contribution;
        return this;
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(participantId);
        out.writeUTF(portfolioId);
        out.writeUTF(instrumentId);
        out.writeDouble(balance);
        out.writeDouble(contribution);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.participantId = in.readUTF();
        this.portfolioId = in.readUTF();
        this.instrumentId = in.readUTF();
        this.balance = in.readDouble();
        this.contribution = in.readDouble();
    }
}
