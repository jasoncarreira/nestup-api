package com.ppi.api.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

@Entity
@Table(name = "instruments")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Instrument extends BaseEntity<Instrument> {
    public static final String MAP_NAME = "instruments";

    private String name;
    private String tickerSymbol;
    private AssetAllocation assetAllocation;
    private int styleBox;

    public Instrument() {
        super(MAP_NAME);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public AssetAllocation getAssetAllocation() {
        return assetAllocation;
    }

    public void setAssetAllocation(AssetAllocation assetAllocation) {
        this.assetAllocation = assetAllocation;
    }

    public int getStyleBox() {
        return styleBox;
    }

    public void setStyleBox(int styleBox) {
        this.styleBox = styleBox;
    }

    @Override
    public Instrument copyFrom(Instrument other) {
        this.name = other.name;
        this.tickerSymbol = other.tickerSymbol;
        this.assetAllocation = new AssetAllocation().copyFrom(other.assetAllocation);
        this.styleBox = other.styleBox;
        return this;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "name='" + name + '\'' +
                ", tickerSymbol='" + tickerSymbol + '\'' +
                ", assetAllocation=" + assetAllocation +
                ", styleBox=" + styleBox +
                "} " + super.toString();
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(name);
        out.writeUTF(tickerSymbol);
        out.writeObject(assetAllocation);
        out.writeInt(styleBox);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.name = in.readUTF();
        this.tickerSymbol = in.readUTF();
        this.assetAllocation = in.readObject();
        this.styleBox = in.readInt();
    }
}
