package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.StockMovementReason;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.StockMovement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovementDTO implements Serializable {

    private Long id;

    @NotNull
    private Long productVariantId;

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

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
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
        if (!(o instanceof StockMovementDTO)) {
            return false;
        }

        StockMovementDTO stockMovementDTO = (StockMovementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockMovementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovementDTO{" +
            "id=" + getId() +
            ", productVariantId=" + getProductVariantId() +
            ", warehouseId=" + getWarehouseId() +
            ", delta=" + getDelta() +
            ", reason='" + getReason() + "'" +
            ", refOrderId=" + getRefOrderId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
