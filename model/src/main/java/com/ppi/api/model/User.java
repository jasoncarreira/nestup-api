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

    @XmlTransient
    @Transient
    private Organization organization;

    @Column(name = "organization_id")
    private String organizationId;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User() {
        super(MAP_NAME);
    }

    public User(String firstName, String lastName, String email, String password, String organizationId, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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

    public Organization getOrganization() {
        if (organization == null && organizationId != null && hazelcastInstance != null) {
            IMap<String, Organization> map = hazelcastInstance.getMap(Organization.MAP_NAME);
            organization = map.get(organizationId);
            if (organization != null) organization.setHazelcastInstance(hazelcastInstance);
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public User copyFrom(User other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.password = other.password;
        this.roles = new HashSet<>(other.roles);
        return this;
    }



    @Override
    public String toString() {
        return "NestupUser{" +
                "id='" + getId() + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
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
        out.writeUTF(organizationId);
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
        this.organizationId = in.readUTF();
        this.roles = in.readObject();
    }
}
