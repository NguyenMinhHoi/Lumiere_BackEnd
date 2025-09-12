package com.lumi.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.Supplement} entity. This class is used
 * in {@link com.lumi.app.web.rest.SupplementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /supplements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter productId;

    private LongFilter supplierId;

    private BigDecimalFilter supplyPrice;

    private StringFilter currency;

    private IntegerFilter leadTimeDays;

    private IntegerFilter minOrderQty;

    private BooleanFilter isPreferred;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public SupplementCriteria() {}

    public SupplementCriteria(SupplementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.supplierId = other.optionalSupplierId().map(LongFilter::copy).orElse(null);
        this.supplyPrice = other.optionalSupplyPrice().map(BigDecimalFilter::copy).orElse(null);
        this.currency = other.optionalCurrency().map(StringFilter::copy).orElse(null);
        this.leadTimeDays = other.optionalLeadTimeDays().map(IntegerFilter::copy).orElse(null);
        this.minOrderQty = other.optionalMinOrderQty().map(IntegerFilter::copy).orElse(null);
        this.isPreferred = other.optionalIsPreferred().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SupplementCriteria copy() {
        return new SupplementCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public LongFilter getSupplierId() {
        return supplierId;
    }

    public Optional<LongFilter> optionalSupplierId() {
        return Optional.ofNullable(supplierId);
    }

    public LongFilter supplierId() {
        if (supplierId == null) {
            setSupplierId(new LongFilter());
        }
        return supplierId;
    }

    public void setSupplierId(LongFilter supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimalFilter getSupplyPrice() {
        return supplyPrice;
    }

    public Optional<BigDecimalFilter> optionalSupplyPrice() {
        return Optional.ofNullable(supplyPrice);
    }

    public BigDecimalFilter supplyPrice() {
        if (supplyPrice == null) {
            setSupplyPrice(new BigDecimalFilter());
        }
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimalFilter supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public StringFilter getCurrency() {
        return currency;
    }

    public Optional<StringFilter> optionalCurrency() {
        return Optional.ofNullable(currency);
    }

    public StringFilter currency() {
        if (currency == null) {
            setCurrency(new StringFilter());
        }
        return currency;
    }

    public void setCurrency(StringFilter currency) {
        this.currency = currency;
    }

    public IntegerFilter getLeadTimeDays() {
        return leadTimeDays;
    }

    public Optional<IntegerFilter> optionalLeadTimeDays() {
        return Optional.ofNullable(leadTimeDays);
    }

    public IntegerFilter leadTimeDays() {
        if (leadTimeDays == null) {
            setLeadTimeDays(new IntegerFilter());
        }
        return leadTimeDays;
    }

    public void setLeadTimeDays(IntegerFilter leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public IntegerFilter getMinOrderQty() {
        return minOrderQty;
    }

    public Optional<IntegerFilter> optionalMinOrderQty() {
        return Optional.ofNullable(minOrderQty);
    }

    public IntegerFilter minOrderQty() {
        if (minOrderQty == null) {
            setMinOrderQty(new IntegerFilter());
        }
        return minOrderQty;
    }

    public void setMinOrderQty(IntegerFilter minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public BooleanFilter getIsPreferred() {
        return isPreferred;
    }

    public Optional<BooleanFilter> optionalIsPreferred() {
        return Optional.ofNullable(isPreferred);
    }

    public BooleanFilter isPreferred() {
        if (isPreferred == null) {
            setIsPreferred(new BooleanFilter());
        }
        return isPreferred;
    }

    public void setIsPreferred(BooleanFilter isPreferred) {
        this.isPreferred = isPreferred;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SupplementCriteria that = (SupplementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(supplierId, that.supplierId) &&
            Objects.equals(supplyPrice, that.supplyPrice) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(leadTimeDays, that.leadTimeDays) &&
            Objects.equals(minOrderQty, that.minOrderQty) &&
            Objects.equals(isPreferred, that.isPreferred) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            productId,
            supplierId,
            supplyPrice,
            currency,
            leadTimeDays,
            minOrderQty,
            isPreferred,
            createdAt,
            updatedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalSupplierId().map(f -> "supplierId=" + f + ", ").orElse("") +
            optionalSupplyPrice().map(f -> "supplyPrice=" + f + ", ").orElse("") +
            optionalCurrency().map(f -> "currency=" + f + ", ").orElse("") +
            optionalLeadTimeDays().map(f -> "leadTimeDays=" + f + ", ").orElse("") +
            optionalMinOrderQty().map(f -> "minOrderQty=" + f + ", ").orElse("") +
            optionalIsPreferred().map(f -> "isPreferred=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
