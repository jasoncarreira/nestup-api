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
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "organizations")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Organization extends BaseEntity {

    public static final String MAP_NAME = "organizations";

    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "organization")
    @XmlTransient
    private transient Collection<NestupUser> users;

    public Organization() {
        super(MAP_NAME);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<NestupUser> getUsers() {
        if (users == null && hazelcastInstance != null) {
            IMap<String, NestupUser> map = hazelcastInstance.getMap(NestupUser.MAP_NAME);
            EntryObject e = new PredicateBuilder().getEntryObject();
            Predicate predicate = e.get("organizationId").is(this.getId());
            users = map.values(predicate);

        }
        return users;
    }

    public void setUsers(Set<NestupUser> users) {
        this.users = users;
    }
}
