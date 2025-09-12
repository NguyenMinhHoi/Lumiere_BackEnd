package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.CustomerTier;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.Customer} entity. This class is used
 * in {@link com.lumi.app.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CustomerTier
     */
    public static class CustomerTierFilter extends Filter<CustomerTier> {

        public CustomerTierFilter() {}

        public CustomerTierFilter(CustomerTierFilter filter) {
            super(filter);
        }

        @Override
        public CustomerTierFilter copy() {
            return new CustomerTierFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter fullName;

    private StringFilter email;

    private StringFilter phone;

    private CustomerTierFilter tier;

    private IntegerFilter points;

    private InstantFilter dob;

    private StringFilter address;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.fullName = other.optionalFullName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.tier = other.optionalTier().map(CustomerTierFilter::copy).orElse(null);
        this.points = other.optionalPoints().map(IntegerFilter::copy).orElse(null);
        this.dob = other.optionalDob().map(InstantFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
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

    public StringFilter getFullName() {
        return fullName;
    }

    public Optional<StringFilter> optionalFullName() {
        return Optional.ofNullable(fullName);
    }

    public StringFilter fullName() {
        if (fullName == null) {
            setFullName(new StringFilter());
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public CustomerTierFilter getTier() {
        return tier;
    }

    public Optional<CustomerTierFilter> optionalTier() {
        return Optional.ofNullable(tier);
    }

    public CustomerTierFilter tier() {
        if (tier == null) {
            setTier(new CustomerTierFilter());
        }
        return tier;
    }

    public void setTier(CustomerTierFilter tier) {
        this.tier = tier;
    }

    public IntegerFilter getPoints() {
        return points;
    }

    public Optional<IntegerFilter> optionalPoints() {
        return Optional.ofNullable(points);
    }

    public IntegerFilter points() {
        if (points == null) {
            setPoints(new IntegerFilter());
        }
        return points;
    }

    public void setPoints(IntegerFilter points) {
        this.points = points;
    }

    public InstantFilter getDob() {
        return dob;
    }

    public Optional<InstantFilter> optionalDob() {
        return Optional.ofNullable(dob);
    }

    public InstantFilter dob() {
        if (dob == null) {
            setDob(new InstantFilter());
        }
        return dob;
    }

    public void setDob(InstantFilter dob) {
        this.dob = dob;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
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
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(tier, that.tier) &&
            Objects.equals(points, that.points) &&
            Objects.equals(dob, that.dob) &&
            Objects.equals(address, that.address) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, email, phone, tier, points, dob, address, createdAt, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalFullName().map(f -> "fullName=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalTier().map(f -> "tier=" + f + ", ").orElse("") +
            optionalPoints().map(f -> "points=" + f + ", ").orElse("") +
            optionalDob().map(f -> "dob=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
