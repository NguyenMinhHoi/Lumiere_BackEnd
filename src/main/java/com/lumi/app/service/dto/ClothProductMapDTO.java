package com.lumi.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.ClothProductMap} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothProductMapDTO implements Serializable {

    private Long id;

    @NotNull
    private Long clothId;

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "0")
    private Double quantity;

    @Size(max = 16)
    private String unit;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
        if (!(o instanceof ClothProductMapDTO)) {
            return false;
        }

        ClothProductMapDTO clothProductMapDTO = (ClothProductMapDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clothProductMapDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothProductMapDTO{" +
            "id=" + getId() +
            ", clothId=" + getClothId() +
            ", productId=" + getProductId() +
            ", quantity=" + getQuantity() +
            ", unit='" + getUnit() + "'" +
            ", note='" + getNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
