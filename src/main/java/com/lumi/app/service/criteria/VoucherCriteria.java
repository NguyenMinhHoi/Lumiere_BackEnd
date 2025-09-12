package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.VoucherStatus;
import com.lumi.app.domain.enumeration.VoucherType;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Criteria class for the {@link com.lumi.app.domain.Voucher} entity. This class is used
 * in {@link com.lumi.app.web.rest.VoucherResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vouchers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VoucherCriteria implements Serializable, Criteria {

    /**
     * Class for filtering VoucherType
     */
    public static class VoucherTypeFilter extends Filter<VoucherType> {

        public VoucherTypeFilter() {}

        public VoucherTypeFilter(VoucherTypeFilter filter) {
            super(filter);
        }

        @Override
        public VoucherTypeFilter copy() {
            return new VoucherTypeFilter(this);
        }
    }

    /**
     * Class for filtering VoucherStatus
     */
    public static class VoucherStatusFilter extends Filter<VoucherStatus> {

        public VoucherStatusFilter() {}

        public VoucherStatusFilter(VoucherStatusFilter filter) {
            super(filter);
        }

        @Override
        public VoucherStatusFilter copy() {
            return new VoucherStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private VoucherTypeFilter discountType;

    private BigDecimalFilter discountValue;

    private BigDecimalFilter minOrderValue;

    private BigDecimalFilter maxDiscountValue;

    private IntegerFilter usageLimit;

    private IntegerFilter usedCount;

    private InstantFilter validFrom;

    private InstantFilter validTo;

    private VoucherStatusFilter status;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public VoucherCriteria() {}

    public VoucherCriteria(VoucherCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.discountType = other.optionalDiscountType().map(VoucherTypeFilter::copy).orElse(null);
        this.discountValue = other.optionalDiscountValue().map(BigDecimalFilter::copy).orElse(null);
        this.minOrderValue = other.optionalMinOrderValue().map(BigDecimalFilter::copy).orElse(null);
        this.maxDiscountValue = other.optionalMaxDiscountValue().map(BigDecimalFilter::copy).orElse(null);
        this.usageLimit = other.optionalUsageLimit().map(IntegerFilter::copy).orElse(null);
        this.usedCount = other.optionalUsedCount().map(IntegerFilter::copy).orElse(null);
        this.validFrom = other.optionalValidFrom().map(InstantFilter::copy).orElse(null);
        this.validTo = other.optionalValidTo().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(VoucherStatusFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VoucherCriteria copy() {
        return new VoucherCriteria(this);
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

    public VoucherTypeFilter getDiscountType() {
        return discountType;
    }

    public Optional<VoucherTypeFilter> optionalDiscountType() {
        return Optional.ofNullable(discountType);
    }

    public VoucherTypeFilter discountType() {
        if (discountType == null) {
            setDiscountType(new VoucherTypeFilter());
        }
        return discountType;
    }

    public void setDiscountType(VoucherTypeFilter discountType) {
        this.discountType = discountType;
    }

    public BigDecimalFilter getDiscountValue() {
        return discountValue;
    }

    public Optional<BigDecimalFilter> optionalDiscountValue() {
        return Optional.ofNullable(discountValue);
    }

    public BigDecimalFilter discountValue() {
        if (discountValue == null) {
            setDiscountValue(new BigDecimalFilter());
        }
        return discountValue;
    }

    public void setDiscountValue(BigDecimalFilter discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimalFilter getMinOrderValue() {
        return minOrderValue;
    }

    public Optional<BigDecimalFilter> optionalMinOrderValue() {
        return Optional.ofNullable(minOrderValue);
    }

    public BigDecimalFilter minOrderValue() {
        if (minOrderValue == null) {
            setMinOrderValue(new BigDecimalFilter());
        }
        return minOrderValue;
    }

    public void setMinOrderValue(BigDecimalFilter minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public BigDecimalFilter getMaxDiscountValue() {
        return maxDiscountValue;
    }

    public Optional<BigDecimalFilter> optionalMaxDiscountValue() {
        return Optional.ofNullable(maxDiscountValue);
    }

    public BigDecimalFilter maxDiscountValue() {
        if (maxDiscountValue == null) {
            setMaxDiscountValue(new BigDecimalFilter());
        }
        return maxDiscountValue;
    }

    public void setMaxDiscountValue(BigDecimalFilter maxDiscountValue) {
        this.maxDiscountValue = maxDiscountValue;
    }

    public IntegerFilter getUsageLimit() {
        return usageLimit;
    }

    public Optional<IntegerFilter> optionalUsageLimit() {
        return Optional.ofNullable(usageLimit);
    }

    public IntegerFilter usageLimit() {
        if (usageLimit == null) {
            setUsageLimit(new IntegerFilter());
        }
        return usageLimit;
    }

    public void setUsageLimit(IntegerFilter usageLimit) {
        this.usageLimit = usageLimit;
    }

    public IntegerFilter getUsedCount() {
        return usedCount;
    }

    public Optional<IntegerFilter> optionalUsedCount() {
        return Optional.ofNullable(usedCount);
    }

    public IntegerFilter usedCount() {
        if (usedCount == null) {
            setUsedCount(new IntegerFilter());
        }
        return usedCount;
    }

    public void setUsedCount(IntegerFilter usedCount) {
        this.usedCount = usedCount;
    }

    public InstantFilter getValidFrom() {
        return validFrom;
    }

    public Optional<InstantFilter> optionalValidFrom() {
        return Optional.ofNullable(validFrom);
    }

    public InstantFilter validFrom() {
        if (validFrom == null) {
            setValidFrom(new InstantFilter());
        }
        return validFrom;
    }

    public void setValidFrom(InstantFilter validFrom) {
        this.validFrom = validFrom;
    }

    public InstantFilter getValidTo() {
        return validTo;
    }

    public Optional<InstantFilter> optionalValidTo() {
        return Optional.ofNullable(validTo);
    }

    public InstantFilter validTo() {
        if (validTo == null) {
            setValidTo(new InstantFilter());
        }
        return validTo;
    }

    public void setValidTo(InstantFilter validTo) {
        this.validTo = validTo;
    }

    public VoucherStatusFilter getStatus() {
        return status;
    }

    public Optional<VoucherStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public VoucherStatusFilter status() {
        if (status == null) {
            setStatus(new VoucherStatusFilter());
        }
        return status;
    }

    public void setStatus(VoucherStatusFilter status) {
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
        final VoucherCriteria that = (VoucherCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(discountType, that.discountType) &&
            Objects.equals(discountValue, that.discountValue) &&
            Objects.equals(minOrderValue, that.minOrderValue) &&
            Objects.equals(maxDiscountValue, that.maxDiscountValue) &&
            Objects.equals(usageLimit, that.usageLimit) &&
            Objects.equals(usedCount, that.usedCount) &&
            Objects.equals(validFrom, that.validFrom) &&
            Objects.equals(validTo, that.validTo) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            discountType,
            discountValue,
            minOrderValue,
            maxDiscountValue,
            usageLimit,
            usedCount,
            validFrom,
            validTo,
            status,
            createdAt,
            updatedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VoucherCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalDiscountType().map(f -> "discountType=" + f + ", ").orElse("") +
            optionalDiscountValue().map(f -> "discountValue=" + f + ", ").orElse("") +
            optionalMinOrderValue().map(f -> "minOrderValue=" + f + ", ").orElse("") +
            optionalMaxDiscountValue().map(f -> "maxDiscountValue=" + f + ", ").orElse("") +
            optionalUsageLimit().map(f -> "usageLimit=" + f + ", ").orElse("") +
            optionalUsedCount().map(f -> "usedCount=" + f + ", ").orElse("") +
            optionalValidFrom().map(f -> "validFrom=" + f + ", ").orElse("") +
            optionalValidTo().map(f -> "validTo=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
