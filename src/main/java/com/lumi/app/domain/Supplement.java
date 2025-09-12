package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Supplement.
 */
@Entity
@Table(name = "supplement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "supplement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supplement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "supply_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal supplyPrice;

    @Size(max = 3)
    @Column(name = "currency", length = 3)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String currency;

    @Min(value = 0)
    @Column(name = "lead_time_days")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer leadTimeDays;

    @Min(value = 1)
    @Column(name = "min_order_qty")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer minOrderQty;

    @NotNull
    @Column(name = "is_preferred", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isPreferred;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Supplement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return this.productId;
    }

    public Supplement productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSupplierId() {
        return this.supplierId;
    }

    public Supplement supplierId(Long supplierId) {
        this.setSupplierId(supplierId);
        return this;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getSupplyPrice() {
        return this.supplyPrice;
    }

    public Supplement supplyPrice(BigDecimal supplyPrice) {
        this.setSupplyPrice(supplyPrice);
        return this;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Supplement currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getLeadTimeDays() {
        return this.leadTimeDays;
    }

    public Supplement leadTimeDays(Integer leadTimeDays) {
        this.setLeadTimeDays(leadTimeDays);
        return this;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public Integer getMinOrderQty() {
        return this.minOrderQty;
    }

    public Supplement minOrderQty(Integer minOrderQty) {
        this.setMinOrderQty(minOrderQty);
        return this;
    }

    public void setMinOrderQty(Integer minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public Boolean getIsPreferred() {
        return this.isPreferred;
    }

    public Supplement isPreferred(Boolean isPreferred) {
        this.setIsPreferred(isPreferred);
        return this;
    }

    public void setIsPreferred(Boolean isPreferred) {
        this.isPreferred = isPreferred;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Supplement createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Supplement updatedAt(Instant updatedAt) {
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
        if (!(o instanceof Supplement)) {
            return false;
        }
        return getId() != null && getId().equals(((Supplement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supplement{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", supplierId=" + getSupplierId() +
            ", supplyPrice=" + getSupplyPrice() +
            ", currency='" + getCurrency() + "'" +
            ", leadTimeDays=" + getLeadTimeDays() +
            ", minOrderQty=" + getMinOrderQty() +
            ", isPreferred='" + getIsPreferred() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
