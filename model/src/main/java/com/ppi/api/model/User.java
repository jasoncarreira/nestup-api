package com.ppi.api.model;

import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.util.*;

/**
 * User
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "users", indexes = {@Index(columnList = "email", unique = true)})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends BaseEntity<User> implements OrganizationOwned, UserOwned {
    public static final String MAP_NAME = "users";

    @Transient
    @XmlTransient
    private String ownerId = id;
    private String firstName;
    private String lastName;
    private String email;
    @XmlTransient
    private String password;
    private int age;
    @Column(name = "retirement_age")
    private int retirementAge;
    private double salary;

    @XmlTransient
    @Transient
    private Organization organization;

    @Column(name = "organization_id")
    private String organizationId;

    @XmlTransient
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Account> accounts;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User() {
        super(MAP_NAME);
    }

    public User(String firstName, String lastName, String email, String password, int age, int retirementAge, double salary, String organizationId, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.retirementAge = retirementAge;
        this.salary = salary;
        this.organizationId = organizationId;
        this.roles = roles;
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        this.ownerId = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Organization getOrganization() {
        if (organization == null && organizationId != null && hazelcastInstance != null) {
            IMap<String, Organization> map = hazelcastInstance.getMap(Organization.MAP_NAME);
            organization = map.get(organizationId);
        }
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = (organization != null) ? organization.getId() : null;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    public void addAccount(Account account) {
        getAccounts().add(account);
        account.setOwner(this);
    }

    public List<Account> getAccounts() {
        if (accounts == null) accounts = new ArrayList<>();
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public void copyFrom(User other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.password = other.password;
        this.age = other.age;
        this.retirementAge = other.retirementAge;
        this.salary = other.salary;
        this.roles = new HashSet<>(other.roles);
    }



    @Override
    public String toString() {
        return "NestupUser{" +
                "id='" + getId() + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", accounts=" + accounts +
                ", roles=" + roles +
                '}';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(ownerId);
        out.writeUTF(firstName);
        out.writeUTF(lastName);
        out.writeUTF(email);
        out.writeUTF(password);
        out.writeInt(age);
        out.writeInt(retirementAge);
        out.writeDouble(salary);
        out.writeUTF(organizationId);
        out.writeObject(accounts);
        out.writeObject(roles);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.ownerId = in.readUTF();
        this.firstName = in.readUTF();
        this.lastName = in.readUTF();
        this.email = in.readUTF();
        this.password = in.readUTF();
        this.age = in.readInt();
        this.retirementAge = in.readInt();
        this.salary = in.readDouble();
        this.organizationId = in.readUTF();
        this.accounts = in.readObject();
        this.roles = in.readObject();
    }
}
