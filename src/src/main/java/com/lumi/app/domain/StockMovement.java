package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.StockMovementReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StockMovement.
 */
@Entity
@Table(name = "stock_movement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "stockmovement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovement implements Serializable {

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
    @Column(name = "delta", nullable = false)
    private Long delta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private StockMovementReason reason;

    @Column(name = "ref_order_id")
    private Long refOrderId;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockMovement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductVariantId() {
        return this.productVariantId;
    }

    public StockMovement productVariantId(Long productVariantId) {
        this.setProductVariantId(productVariantId);
        return this;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Long getWarehouseId() {
        return this.warehouseId;
    }

    public StockMovement warehouseId(Long warehouseId) {
        this.setWarehouseId(warehouseId);
        return this;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getDelta() {
        return this.delta;
    }

    public StockMovement delta(Long delta) {
        this.setDelta(delta);
        return this;
    }

    public void setDelta(Long delta) {
        this.delta = delta;
    }

    public StockMovementReason getReason() {
        return this.reason;
    }

    public StockMovement reason(StockMovementReason reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(StockMovementReason reason) {
        this.reason = reason;
    }

    public Long getRefOrderId() {
        return this.refOrderId;
    }

    public StockMovement refOrderId(Long refOrderId) {
        this.setRefOrderId(refOrderId);
        return this;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StockMovement createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMovement)) {
            return false;
        }
        return getId() != null && getId().equals(((StockMovement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovement{" +
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
