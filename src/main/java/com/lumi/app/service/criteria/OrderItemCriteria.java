package com.lumi.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.OrderItem} entity. This class is used
 * in {@link com.lumi.app.web.rest.OrderItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter quantity;

    private BigDecimalFilter unitPrice;

    private BigDecimalFilter totalPrice;

    private StringFilter nameSnapshot;

    private StringFilter skuSnapshot;

    private LongFilter orderId;

    private LongFilter variantId;

    private Boolean distinct;

    public OrderItemCriteria() {}

    public OrderItemCriteria(OrderItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(LongFilter::copy).orElse(null);
        this.unitPrice = other.optionalUnitPrice().map(BigDecimalFilter::copy).orElse(null);
        this.totalPrice = other.optionalTotalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.nameSnapshot = other.optionalNameSnapshot().map(StringFilter::copy).orElse(null);
        this.skuSnapshot = other.optionalSkuSnapshot().map(StringFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.variantId = other.optionalVariantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrderItemCriteria copy() {
        return new OrderItemCriteria(this);
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

    public LongFilter getQuantity() {
        return quantity;
    }

    public Optional<LongFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public LongFilter quantity() {
        if (quantity == null) {
            setQuantity(new LongFilter());
        }
        return quantity;
    }

    public void setQuantity(LongFilter quantity) {
        this.quantity = quantity;
    }

    public BigDecimalFilter getUnitPrice() {
        return unitPrice;
    }

    public Optional<BigDecimalFilter> optionalUnitPrice() {
        return Optional.ofNullable(unitPrice);
    }

    public BigDecimalFilter unitPrice() {
        if (unitPrice == null) {
            setUnitPrice(new BigDecimalFilter());
        }
        return unitPrice;
    }

    public void setUnitPrice(BigDecimalFilter unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimalFilter getTotalPrice() {
        return totalPrice;
    }

    public Optional<BigDecimalFilter> optionalTotalPrice() {
        return Optional.ofNullable(totalPrice);
    }

    public BigDecimalFilter totalPrice() {
        if (totalPrice == null) {
            setTotalPrice(new BigDecimalFilter());
        }
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public StringFilter getNameSnapshot() {
        return nameSnapshot;
    }

    public Optional<StringFilter> optionalNameSnapshot() {
        return Optional.ofNullable(nameSnapshot);
    }

    public StringFilter nameSnapshot() {
        if (nameSnapshot == null) {
            setNameSnapshot(new StringFilter());
        }
        return nameSnapshot;
    }

    public void setNameSnapshot(StringFilter nameSnapshot) {
        this.nameSnapshot = nameSnapshot;
    }

    public StringFilter getSkuSnapshot() {
        return skuSnapshot;
    }

    public Optional<StringFilter> optionalSkuSnapshot() {
        return Optional.ofNullable(skuSnapshot);
    }

    public StringFilter skuSnapshot() {
        if (skuSnapshot == null) {
            setSkuSnapshot(new StringFilter());
        }
        return skuSnapshot;
    }

    public void setSkuSnapshot(StringFilter skuSnapshot) {
        this.skuSnapshot = skuSnapshot;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getVariantId() {
        return variantId;
    }

    public Optional<LongFilter> optionalVariantId() {
        return Optional.ofNullable(variantId);
    }

    public LongFilter variantId() {
        if (variantId == null) {
            setVariantId(new LongFilter());
        }
        return variantId;
    }

    public void setVariantId(LongFilter variantId) {
        this.variantId = variantId;
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
        final OrderItemCriteria that = (OrderItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(nameSnapshot, that.nameSnapshot) &&
            Objects.equals(skuSnapshot, that.skuSnapshot) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(variantId, that.variantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, unitPrice, totalPrice, nameSnapshot, skuSnapshot, orderId, variantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalUnitPrice().map(f -> "unitPrice=" + f + ", ").orElse("") +
            optionalTotalPrice().map(f -> "totalPrice=" + f + ", ").orElse("") +
            optionalNameSnapshot().map(f -> "nameSnapshot=" + f + ", ").orElse("") +
            optionalSkuSnapshot().map(f -> "skuSnapshot=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalVariantId().map(f -> "variantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
