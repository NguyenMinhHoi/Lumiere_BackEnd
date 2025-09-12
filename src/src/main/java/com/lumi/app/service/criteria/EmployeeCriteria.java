package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.EmployeeRole;
import com.lumi.app.domain.enumeration.EmployeeStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.Employee} entity. This class is used
 * in {@link com.lumi.app.web.rest.EmployeeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /employees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EmployeeRole
     */
    public static class EmployeeRoleFilter extends Filter<EmployeeRole> {

        public EmployeeRoleFilter() {}

        public EmployeeRoleFilter(EmployeeRoleFilter filter) {
            super(filter);
        }

        @Override
        public EmployeeRoleFilter copy() {
            return new EmployeeRoleFilter(this);
        }
    }

    /**
     * Class for filtering EmployeeStatus
     */
    public static class EmployeeStatusFilter extends Filter<EmployeeStatus> {

        public EmployeeStatusFilter() {}

        public EmployeeStatusFilter(EmployeeStatusFilter filter) {
            super(filter);
        }

        @Override
        public EmployeeStatusFilter copy() {
            return new EmployeeStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter fullName;

    private StringFilter email;

    private StringFilter phone;

    private EmployeeRoleFilter role;

    private EmployeeStatusFilter status;

    private StringFilter department;

    private InstantFilter joinedAt;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public EmployeeCriteria() {}

    public EmployeeCriteria(EmployeeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.fullName = other.optionalFullName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.role = other.optionalRole().map(EmployeeRoleFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EmployeeStatusFilter::copy).orElse(null);
        this.department = other.optionalDepartment().map(StringFilter::copy).orElse(null);
        this.joinedAt = other.optionalJoinedAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EmployeeCriteria copy() {
        return new EmployeeCriteria(this);
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

    public EmployeeRoleFilter getRole() {
        return role;
    }

    public Optional<EmployeeRoleFilter> optionalRole() {
        return Optional.ofNullable(role);
    }

    public EmployeeRoleFilter role() {
        if (role == null) {
            setRole(new EmployeeRoleFilter());
        }
        return role;
    }

    public void setRole(EmployeeRoleFilter role) {
        this.role = role;
    }

    public EmployeeStatusFilter getStatus() {
        return status;
    }

    public Optional<EmployeeStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EmployeeStatusFilter status() {
        if (status == null) {
            setStatus(new EmployeeStatusFilter());
        }
        return status;
    }

    public void setStatus(EmployeeStatusFilter status) {
        this.status = status;
    }

    public StringFilter getDepartment() {
        return department;
    }

    public Optional<StringFilter> optionalDepartment() {
        return Optional.ofNullable(department);
    }

    public StringFilter department() {
        if (department == null) {
            setDepartment(new StringFilter());
        }
        return department;
    }

    public void setDepartment(StringFilter department) {
        this.department = department;
    }

    public InstantFilter getJoinedAt() {
        return joinedAt;
    }

    public Optional<InstantFilter> optionalJoinedAt() {
        return Optional.ofNullable(joinedAt);
    }

    public InstantFilter joinedAt() {
        if (joinedAt == null) {
            setJoinedAt(new InstantFilter());
        }
        return joinedAt;
    }

    public void setJoinedAt(InstantFilter joinedAt) {
        this.joinedAt = joinedAt;
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
        final EmployeeCriteria that = (EmployeeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(role, that.role) &&
            Objects.equals(status, that.status) &&
            Objects.equals(department, that.department) &&
            Objects.equals(joinedAt, that.joinedAt) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, email, phone, role, status, department, joinedAt, createdAt, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalFullName().map(f -> "fullName=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalDepartment().map(f -> "department=" + f + ", ").orElse("") +
            optionalJoinedAt().map(f -> "joinedAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
