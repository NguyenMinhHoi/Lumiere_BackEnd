package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.AuditAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AuditHistory.
 */
@Entity
@Table(name = "audit_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "audithistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "entity_name", length = 128, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String entityName;

    @NotNull
    @Size(max = 64)
    @Column(name = "entity_id", length = 64, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String entityId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AuditAction action;

    @Lob
    @Column(name = "old_value")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String oldValue;

    @Lob
    @Column(name = "new_value")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String newValue;

    @Size(max = 64)
    @Column(name = "performed_by", length = 64)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String performedBy;

    @NotNull
    @Column(name = "performed_at", nullable = false)
    private Instant performedAt;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String ipAddress;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public AuditHistory entityName(String entityName) {
        this.setEntityName(entityName);
        return this;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public AuditHistory entityId(String entityId) {
        this.setEntityId(entityId);
        return this;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public AuditAction getAction() {
        return this.action;
    }

    public AuditHistory action(AuditAction action) {
        this.setAction(action);
        return this;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public AuditHistory oldValue(String oldValue) {
        this.setOldValue(oldValue);
        return this;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public AuditHistory newValue(String newValue) {
        this.setNewValue(newValue);
        return this;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getPerformedBy() {
        return this.performedBy;
    }

    public AuditHistory performedBy(String performedBy) {
        this.setPerformedBy(performedBy);
        return this;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public Instant getPerformedAt() {
        return this.performedAt;
    }

    public AuditHistory performedAt(Instant performedAt) {
        this.setPerformedAt(performedAt);
        return this;
    }

    public void setPerformedAt(Instant performedAt) {
        this.performedAt = performedAt;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public AuditHistory ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditHistory{" +
            "id=" + getId() +
            ", entityName='" + getEntityName() + "'" +
            ", entityId='" + getEntityId() + "'" +
            ", action='" + getAction() + "'" +
            ", oldValue='" + getOldValue() + "'" +
            ", newValue='" + getNewValue() + "'" +
            ", performedBy='" + getPerformedBy() + "'" +
            ", performedAt='" + getPerformedAt() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            "}";
    }
}
