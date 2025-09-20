package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.ClothStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.Cloth} entity. This class is used
 * in {@link com.lumi.app.web.rest.ClothResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cloths?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ClothStatus
     */
    public static class ClothStatusFilter extends Filter<ClothStatus> {

        public ClothStatusFilter() {}

        public ClothStatusFilter(ClothStatusFilter filter) {
            super(filter);
        }

        @Override
        public ClothStatusFilter copy() {
            return new ClothStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private StringFilter material;

    private StringFilter color;

    private DoubleFilter width;

    private DoubleFilter length;

    private StringFilter unit;

    private ClothStatusFilter status;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public ClothCriteria() {}

    public ClothCriteria(ClothCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.material = other.optionalMaterial().map(StringFilter::copy).orElse(null);
        this.color = other.optionalColor().map(StringFilter::copy).orElse(null);
        this.width = other.optionalWidth().map(DoubleFilter::copy).orElse(null);
        this.length = other.optionalLength().map(DoubleFilter::copy).orElse(null);
        this.unit = other.optionalUnit().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ClothStatusFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ClothCriteria copy() {
        return new ClothCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getMaterial() {
        return material;
    }

    public Optional<StringFilter> optionalMaterial() {
        return Optional.ofNullable(material);
    }

    public StringFilter material() {
        if (material == null) {
            setMaterial(new StringFilter());
        }
        return material;
    }

    public void setMaterial(StringFilter material) {
        this.material = material;
    }

    public StringFilter getColor() {
        return color;
    }

    public Optional<StringFilter> optionalColor() {
        return Optional.ofNullable(color);
    }

    public StringFilter color() {
        if (color == null) {
            setColor(new StringFilter());
        }
        return color;
    }

    public void setColor(StringFilter color) {
        this.color = color;
    }

    public DoubleFilter getWidth() {
        return width;
    }

    public Optional<DoubleFilter> optionalWidth() {
        return Optional.ofNullable(width);
    }

    public DoubleFilter width() {
        if (width == null) {
            setWidth(new DoubleFilter());
        }
        return width;
    }

    public void setWidth(DoubleFilter width) {
        this.width = width;
    }

    public DoubleFilter getLength() {
        return length;
    }

    public Optional<DoubleFilter> optionalLength() {
        return Optional.ofNullable(length);
    }

    public DoubleFilter length() {
        if (length == null) {
            setLength(new DoubleFilter());
        }
        return length;
    }

    public void setLength(DoubleFilter length) {
        this.length = length;
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

    public ClothStatusFilter getStatus() {
        return status;
    }

    public Optional<ClothStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ClothStatusFilter status() {
        if (status == null) {
            setStatus(new ClothStatusFilter());
        }
        return status;
    }

    public void setStatus(ClothStatusFilter status) {
        this.status = status;
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
        final ClothCriteria that = (ClothCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(material, that.material) &&
            Objects.equals(color, that.color) &&
            Objects.equals(width, that.width) &&
            Objects.equals(length, that.length) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, material, color, width, length, unit, status, createdAt, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalMaterial().map(f -> "material=" + f + ", ").orElse("") +
            optionalColor().map(f -> "color=" + f + ", ").orElse("") +
            optionalWidth().map(f -> "width=" + f + ", ").orElse("") +
            optionalLength().map(f -> "length=" + f + ", ").orElse("") +
            optionalUnit().map(f -> "unit=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
