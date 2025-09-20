package com.lumi.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.ClothInventory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothInventoryDTO implements Serializable {

    private Long id;

    @NotNull
    private Long clothId;

    @NotNull
    private Long warehouseId;

    @NotNull
    @Min(value = 0L)
    private Long quantity;

    @NotNull
    private Instant updatedAt;

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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClothInventoryDTO)) {
            return false;
        }

        ClothInventoryDTO clothInventoryDTO = (ClothInventoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clothInventoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothInventoryDTO{" +
            "id=" + getId() +
            ", clothId=" + getClothId() +
            ", warehouseId=" + getWarehouseId() +
            ", quantity=" + getQuantity() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
