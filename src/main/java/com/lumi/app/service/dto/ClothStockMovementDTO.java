package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.StockMovementReason;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.ClothStockMovement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothStockMovementDTO implements Serializable {

    private Long id;

    @NotNull
    private Long clothId;

    @NotNull
    private Long warehouseId;

    @NotNull
    private Long delta;

    @NotNull
    private StockMovementReason reason;

    private Long refOrderId;

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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getDelta() {
        return delta;
    }

    public void setDelta(Long delta) {
        this.delta = delta;
    }

    public StockMovementReason getReason() {
        return reason;
    }

    public void setReason(StockMovementReason reason) {
        this.reason = reason;
    }

    public Long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
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
        if (!(o instanceof ClothStockMovementDTO)) {
            return false;
        }

        ClothStockMovementDTO clothStockMovementDTO = (ClothStockMovementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clothStockMovementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothStockMovementDTO{" +
            "id=" + getId() +
            ", clothId=" + getClothId() +
            ", warehouseId=" + getWarehouseId() +
            ", delta=" + getDelta() +
            ", reason='" + getReason() + "'" +
            ", refOrderId=" + getRefOrderId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
