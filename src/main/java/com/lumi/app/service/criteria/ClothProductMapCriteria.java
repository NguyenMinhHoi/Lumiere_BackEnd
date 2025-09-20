package com.lumi.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.ClothProductMap} entity. This class is used
 * in {@link com.lumi.app.web.rest.ClothProductMapResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cloth-product-maps?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothProductMapCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter clothId;

    private LongFilter productId;

    private DoubleFilter quantity;

    private StringFilter unit;

    private StringFilter note;

    private InstantFilter createdAt;

    private Boolean distinct;

    public ClothProductMapCriteria() {}

    public ClothProductMapCriteria(ClothProductMapCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.clothId = other.optionalClothId().map(LongFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(DoubleFilter::copy).orElse(null);
        this.unit = other.optionalUnit().map(StringFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ClothProductMapCriteria copy() {
        return new ClothProductMapCriteria(this);
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

    public LongFilter getClothId() {
        return clothId;
    }

    public Optional<LongFilter> optionalClothId() {
        return Optional.ofNullable(clothId);
    }

    public LongFilter clothId() {
        if (clothId == null) {
            setClothId(new LongFilter());
        }
        return clothId;
    }

    public void setClothId(LongFilter clothId) {
        this.clothId = clothId;
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

    public DoubleFilter getQuantity() {
        return quantity;
    }

    public Optional<DoubleFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public DoubleFilter quantity() {
        if (quantity == null) {
            setQuantity(new DoubleFilter());
        }
        return quantity;
    }

    public void setQuantity(DoubleFilter quantity) {
        this.quantity = quantity;
    }

    public StringFilter getUnit() {
        return unit;
    }

    public Optional<StringFilter> optionalUnit() {
        return Optional.ofNullable(unit);
    }

    public StringFilter unit() {
        if (unit == null) {
            setUnit(new StringFilter());
        }
        return unit;
    }

    public void setUnit(StringFilter unit) {
        this.unit = unit;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
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
        final ClothProductMapCriteria that = (ClothProductMapCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(clothId, that.clothId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(note, that.note) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clothId, productId, quantity, unit, note, createdAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothProductMapCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalClothId().map(f -> "clothId=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalUnit().map(f -> "unit=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
