package com.ppi.api.model;

import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
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

@Entity
@Table(name = "organizations")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Organization extends BaseEntity<Organization> implements OrganizationOwned {

    public static final String MAP_NAME = "organizations";

    @Column(name = "parent_org_id")
    private String parentId;

    @XmlTransient
    @Transient
    private Organization parentOrganization;

    @XmlTransient
    @Transient
    private Set<Organization> childOrganizations;

    private String name;

    @Transient
    @XmlTransient
    private Collection<User> users;

    public Organization() {
        super(MAP_NAME);
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @Override
    public String getOrganizationId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Organization getParentOrganization() {
        if (parentOrganization == null && parentId != null && hazelcastInstance != null) {
            parentOrganization = (Organization) hazelcastInstance.getMap(MAP_NAME).get(parentId);
            parentOrganization.setHazelcastInstance(this.hazelcastInstance);
        }
        return parentOrganization;
    }

    private void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
        this.parentId = (parentOrganization != null) ? parentOrganization.getId() : null;
    }

    public Set<Organization> getChildOrganizations() {
        if (childOrganizations == null) {
            if (hazelcastInstance != null) {
                IMap<String, Organization> map = hazelcastInstance.getMap(MAP_NAME);
                EntryObject e = new PredicateBuilder().getEntryObject();
                Predicate predicate = e.get("parentId").equal(this.getId());
                childOrganizations = new HashSet<>(map.values(predicate));
                childOrganizations.forEach((child) -> child.setHazelcastInstance(this.hazelcastInstance));
            } else {
                childOrganizations = new HashSet<>();
            }
        }
        return childOrganizations;
    }

    private void setChildOrganizations(Set<Organization> childOrganizations) {
        this.childOrganizations = childOrganizations;
    }

    public void addChildOrganization(Organization childOrganization) {
        getChildOrganizations().add(childOrganization);
        childOrganization.setParentOrganization(this);
    }

    public void addUser(User user) {
        user.setOrganization(this);
        getUsers().add(user);
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public Collection<User> getUsers() {
        if (users == null && hazelcastInstance != null) {
            IMap<String, User> map = hazelcastInstance.getMap(User.MAP_NAME);
            EntryObject e = new PredicateBuilder().getEntryObject();
            Predicate predicate = e.get("organizationId").is(this.getId());
            users = map.values(predicate);
            users.forEach((user) -> user.setHazelcastInstance(hazelcastInstance));
        }
        if (users == null) users = new HashSet<>();
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public void copyFrom(Organization other) {
        this.name = other.name;
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(parentId);
        out.writeUTF(name);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.parentId = in.readUTF();
        this.name = in.readUTF();
    }
}
