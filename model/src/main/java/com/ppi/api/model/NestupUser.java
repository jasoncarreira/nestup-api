package com.ppi.api.model;

import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Set;

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
public class NestupUser extends BaseEntity {
    public static final String MAP_NAME = "users";

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int age;
    @Column(name = "retirement_age")
    private int retirementAge;
    private double salary;

    @ManyToOne(fetch = FetchType.EAGER)
    @XmlTransient
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Transient
    @XmlTransient
    @Column(name = "organization_id", insertable = false, updatable = false)
    private String organizationId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Account> accounts;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public NestupUser() {
        super(MAP_NAME);
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

    public String getOrganizationId() {
        return organizationId;
    }

    public List<Account> getAccounts() {
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
}
