package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ClothInventory.
 */
@Entity
@Table(name = "cloth_inventory")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "clothinventory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "cloth_id", nullable = false)
    private Long clothId;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @NotNull
    @Min(value = 0L)
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClothInventory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClothId() {
        return this.clothId;
    }

    public ClothInventory clothId(Long clothId) {
        this.setClothId(clothId);
        return this;
    }

    public void setClothId(Long clothId) {
        this.clothId = clothId;
    }

    public Long getWarehouseId() {
        return this.warehouseId;
    }

    public ClothInventory warehouseId(Long warehouseId) {
        this.setWarehouseId(warehouseId);
        return this;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public ClothInventory quantity(Long quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ClothInventory updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClothInventory)) {
            return false;
        }
        return getId() != null && getId().equals(((ClothInventory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothInventory{" +
            "id=" + getId() +
            ", clothId=" + getClothId() +
            ", warehouseId=" + getWarehouseId() +
            ", quantity=" + getQuantity() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
