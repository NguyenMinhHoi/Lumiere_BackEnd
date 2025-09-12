package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Inventory.
 */
@Entity
@Table(name = "inventory")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "inventory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

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

    public Inventory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductVariantId() {
        return this.productVariantId;
    }

    public Inventory productVariantId(Long productVariantId) {
        this.setProductVariantId(productVariantId);
        return this;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Long getWarehouseId() {
        return this.warehouseId;
    }

    public Inventory warehouseId(Long warehouseId) {
        this.setWarehouseId(warehouseId);
        return this;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public Inventory quantity(Long quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Inventory updatedAt(Instant updatedAt) {
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
        if (!(o instanceof Inventory)) {
            return false;
        }
        return getId() != null && getId().equals(((Inventory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inventory{" +
            "id=" + getId() +
            ", productVariantId=" + getProductVariantId() +
            ", warehouseId=" + getWarehouseId() +
            ", quantity=" + getQuantity() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
