package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.AuditAction;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.AuditHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditHistoryDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 128)
    private String entityName;

    @NotNull
    @Size(max = 64)
    private String entityId;

    @NotNull
    private AuditAction action;

    @Lob
    private String oldValue;

    @Lob
    private String newValue;

    @Size(max = 64)
    private String performedBy;

    @NotNull
    private Instant performedAt;

    @Size(max = 45)
    private String ipAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Instant performedAt) {
        this.performedAt = performedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditHistoryDTO)) {
            return false;
        }

        AuditHistoryDTO auditHistoryDTO = (AuditHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditHistoryDTO{" +
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
