package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.AuditAction;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Criteria class for the {@link com.lumi.app.domain.AuditHistory} entity. This class is used
 * in {@link com.lumi.app.web.rest.AuditHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /audit-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditHistoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AuditAction
     */
    public static class AuditActionFilter extends Filter<AuditAction> {

        public AuditActionFilter() {}

        public AuditActionFilter(AuditActionFilter filter) {
            super(filter);
        }

        @Override
        public AuditActionFilter copy() {
            return new AuditActionFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter entityName;

    private StringFilter entityId;

    private AuditActionFilter action;

    private StringFilter performedBy;

    private InstantFilter performedAt;

    private StringFilter ipAddress;

    private Boolean distinct;

    public AuditHistoryCriteria() {}

    public AuditHistoryCriteria(AuditHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.entityName = other.optionalEntityName().map(StringFilter::copy).orElse(null);
        this.entityId = other.optionalEntityId().map(StringFilter::copy).orElse(null);
        this.action = other.optionalAction().map(AuditActionFilter::copy).orElse(null);
        this.performedBy = other.optionalPerformedBy().map(StringFilter::copy).orElse(null);
        this.performedAt = other.optionalPerformedAt().map(InstantFilter::copy).orElse(null);
        this.ipAddress = other.optionalIpAddress().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AuditHistoryCriteria copy() {
        return new AuditHistoryCriteria(this);
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

    public StringFilter getEntityName() {
        return entityName;
    }

    public Optional<StringFilter> optionalEntityName() {
        return Optional.ofNullable(entityName);
    }

    public StringFilter entityName() {
        if (entityName == null) {
            setEntityName(new StringFilter());
        }
        return entityName;
    }

    public void setEntityName(StringFilter entityName) {
        this.entityName = entityName;
    }

    public StringFilter getEntityId() {
        return entityId;
    }

    public Optional<StringFilter> optionalEntityId() {
        return Optional.ofNullable(entityId);
    }

    public StringFilter entityId() {
        if (entityId == null) {
            setEntityId(new StringFilter());
        }
        return entityId;
    }

    public void setEntityId(StringFilter entityId) {
        this.entityId = entityId;
    }

    public AuditActionFilter getAction() {
        return action;
    }

    public Optional<AuditActionFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public AuditActionFilter action() {
        if (action == null) {
            setAction(new AuditActionFilter());
        }
        return action;
    }

    public void setAction(AuditActionFilter action) {
        this.action = action;
    }

    public StringFilter getPerformedBy() {
        return performedBy;
    }

    public Optional<StringFilter> optionalPerformedBy() {
        return Optional.ofNullable(performedBy);
    }

    public StringFilter performedBy() {
        if (performedBy == null) {
            setPerformedBy(new StringFilter());
        }
        return performedBy;
    }

    public void setPerformedBy(StringFilter performedBy) {
        this.performedBy = performedBy;
    }

    public InstantFilter getPerformedAt() {
        return performedAt;
    }

    public Optional<InstantFilter> optionalPerformedAt() {
        return Optional.ofNullable(performedAt);
    }

    public InstantFilter performedAt() {
        if (performedAt == null) {
            setPerformedAt(new InstantFilter());
        }
        return performedAt;
    }

    public void setPerformedAt(InstantFilter performedAt) {
        this.performedAt = performedAt;
    }

    public StringFilter getIpAddress() {
        return ipAddress;
    }

    public Optional<StringFilter> optionalIpAddress() {
        return Optional.ofNullable(ipAddress);
    }

    public StringFilter ipAddress() {
        if (ipAddress == null) {
            setIpAddress(new StringFilter());
        }
        return ipAddress;
    }

    public void setIpAddress(StringFilter ipAddress) {
        this.ipAddress = ipAddress;
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
        final AuditHistoryCriteria that = (AuditHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(entityName, that.entityName) &&
            Objects.equals(entityId, that.entityId) &&
            Objects.equals(action, that.action) &&
            Objects.equals(performedBy, that.performedBy) &&
            Objects.equals(performedAt, that.performedAt) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityName, entityId, action, performedBy, performedAt, ipAddress, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEntityName().map(f -> "entityName=" + f + ", ").orElse("") +
            optionalEntityId().map(f -> "entityId=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalPerformedBy().map(f -> "performedBy=" + f + ", ").orElse("") +
            optionalPerformedAt().map(f -> "performedAt=" + f + ", ").orElse("") +
            optionalIpAddress().map(f -> "ipAddress=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
