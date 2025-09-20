package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.AuditAction;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.ClothAudit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothAuditDTO implements Serializable {

    private Long id;

    @NotNull
    private Long clothId;

    @NotNull
    private Long supplierId;

    @NotNull
    private Long productId;

    @NotNull
    private AuditAction action;

    @NotNull
    @DecimalMin(value = "0")
    private Double quantity;

    @Size(max = 16)
    private String unit;

    @NotNull
    private Instant sentAt;

    @Size(max = 255)
    private String note;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClothId() {
        return clothId;
    }

    public void setClothId(Long clothId) {
        this.clothId = clothId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClothAuditDTO)) {
            return false;
        }

        ClothAuditDTO clothAuditDTO = (ClothAuditDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clothAuditDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothAuditDTO{" +
            "id=" + getId() +
            ", clothId=" + getClothId() +
            ", supplierId=" + getSupplierId() +
            ", productId=" + getProductId() +
            ", action='" + getAction() + "'" +
            ", quantity=" + getQuantity() +
            ", unit='" + getUnit() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", note='" + getNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
