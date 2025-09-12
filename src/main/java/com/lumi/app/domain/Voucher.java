package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.VoucherStatus;
import com.lumi.app.domain.enumeration.VoucherType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Voucher.
 */
@Entity
@Table(name = "voucher")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "voucher")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    @Column(name = "code", length = 64, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private VoucherType discountType;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "discount_value", precision = 21, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_order_value", precision = 21, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_value", precision = 21, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "usage_limit")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer usageLimit;

    @NotNull
    @Min(value = 0)
    @Column(name = "used_count", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer usedCount;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @NotNull
    @Column(name = "valid_to", nullable = false)
    private Instant validTo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private VoucherStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Voucher id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Voucher code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherType getDiscountType() {
        return this.discountType;
    }

    public Voucher discountType(VoucherType discountType) {
        this.setDiscountType(discountType);
        return this;
    }

    public void setDiscountType(VoucherType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return this.discountValue;
    }

    public Voucher discountValue(BigDecimal discountValue) {
        this.setDiscountValue(discountValue);
        return this;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderValue() {
        return this.minOrderValue;
    }

    public Voucher minOrderValue(BigDecimal minOrderValue) {
        this.setMinOrderValue(minOrderValue);
        return this;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public BigDecimal getMaxDiscountValue() {
        return this.maxDiscountValue;
    }

    public Voucher maxDiscountValue(BigDecimal maxDiscountValue) {
        this.setMaxDiscountValue(maxDiscountValue);
        return this;
    }

    public void setMaxDiscountValue(BigDecimal maxDiscountValue) {
        this.maxDiscountValue = maxDiscountValue;
    }

    public Integer getUsageLimit() {
        return this.usageLimit;
    }

    public Voucher usageLimit(Integer usageLimit) {
        this.setUsageLimit(usageLimit);
        return this;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsedCount() {
        return this.usedCount;
    }

    public Voucher usedCount(Integer usedCount) {
        this.setUsedCount(usedCount);
        return this;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public Voucher validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidTo() {
        return this.validTo;
    }

    public Voucher validTo(Instant validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public VoucherStatus getStatus() {
        return this.status;
    }

    public Voucher status(VoucherStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Voucher createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Voucher updatedAt(Instant updatedAt) {
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
        if (!(o instanceof Voucher)) {
            return false;
        }
        return getId() != null && getId().equals(((Voucher) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Voucher{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", discountType='" + getDiscountType() + "'" +
            ", discountValue=" + getDiscountValue() +
            ", minOrderValue=" + getMinOrderValue() +
            ", maxDiscountValue=" + getMaxDiscountValue() +
            ", usageLimit=" + getUsageLimit() +
            ", usedCount=" + getUsedCount() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
