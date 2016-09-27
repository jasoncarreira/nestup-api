package com.ppi.api.model;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.DataSerializable;

import javax.annotation.Generated;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Persistable
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@MappedSuperclass
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseEntity<T extends BaseEntity> implements Serializable, DataSerializable {

    @Transient
    protected transient HazelcastInstance hazelcastInstance;
    @Transient
    private transient String mapName;

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "VARCHAR(64)")
    protected String id = UUID.randomUUID().toString();

    protected BaseEntity() {
    }

    public BaseEntity(String mapName) {
        this.mapName = mapName;
    }

//    @Column(name = "created_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdAt;
//
//    @Column(name = "updated_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date updatedAt;


    public String getMapName() {
        return mapName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Date createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Date getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Date updatedAt) {
//        this.updatedAt = updatedAt;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * Copy the fields from the other instance. Should return "this" to enable fluent API
     * @param other other instance to copy from
     * @return this
     */
    public abstract T copyFrom(T other);
}
