package com.ppi.api.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "participants")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Participant extends BaseEntity<Participant> implements UserOwned {

    public static final String MAP_NAME = "participants";

    @Transient
    @XmlTransient
    private User user;

    private int age;
    @Column(name = "retirement_age")
    private int retirementAge;
    private double salary;

    @Column(name = "organization_id")
    private String organizationId;

    @XmlTransient
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "owner")
    private Set<Portfolio> portfolios;

    public Participant() {
        super(MAP_NAME);
    }

    public User getUser() {
        if (user == null && hazelcastInstance != null) {
            this.user = (User) hazelcastInstance.getMap(User.MAP_NAME).get(id);
            if (this.user != null) this.user.setHazelcastInstance(hazelcastInstance);
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.id = user.getId();
            this.organizationId = user.getOrganizationId();
        } else {
            this.organizationId = null;
        }
    }

    @Override
    public String getOwnerId() {
        return getId();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getRetirementAge() {
        return retirementAge;
    }

    public void setRetirementAge(int retirementAge) {
        this.retirementAge = retirementAge;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Set<Portfolio> getPortfolios() {
        if (portfolios == null) portfolios = new HashSet<>();
        return portfolios;
    }

    public void setPortfolios(Set<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public void addPortfolio(Portfolio portfolio) {
        getPortfolios().add(portfolio);
        portfolio.setOwner(this);
    }

    /**
     * Copy the fields from the other instance. Should return "this" to enable fluent API
     *
     * @param other other instance to copy from
     * @return this
     */
    @Override
    public Participant copyFrom(Participant other) {
        setUser(other.user);
        this.age = other.age;
        this.retirementAge = other.retirementAge;
        this.salary = other.salary;
        this.organizationId = other.organizationId;
        this.portfolios = new HashSet<>(other.portfolios);
        return this;
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(organizationId);
        out.writeInt(age);
        out.writeInt(retirementAge);
        out.writeDouble(salary);
        out.writeObject(portfolios);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.organizationId = in.readUTF();
        this.age = in.readInt();
        this.retirementAge = in.readInt();
        this.salary = in.readDouble();
        this.portfolios = in.readObject();
    }
}
