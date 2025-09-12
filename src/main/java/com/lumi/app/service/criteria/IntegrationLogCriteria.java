package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.AppType;
import com.lumi.app.domain.enumeration.IntegrationStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Criteria class for the {@link com.lumi.app.domain.IntegrationLog} entity. This class is used
 * in {@link com.lumi.app.web.rest.IntegrationLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /integration-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AppType
     */
    public static class AppTypeFilter extends Filter<AppType> {

        public AppTypeFilter() {}

        public AppTypeFilter(AppTypeFilter filter) {
            super(filter);
        }

        @Override
        public AppTypeFilter copy() {
            return new AppTypeFilter(this);
        }
    }

    /**
     * Class for filtering IntegrationStatus
     */
    public static class IntegrationStatusFilter extends Filter<IntegrationStatus> {

        public IntegrationStatusFilter() {}

        public IntegrationStatusFilter(IntegrationStatusFilter filter) {
            super(filter);
        }

        @Override
        public IntegrationStatusFilter copy() {
            return new IntegrationStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private AppTypeFilter sourceApp;

    private AppTypeFilter targetApp;

    private IntegrationStatusFilter status;

    private IntegerFilter retries;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public IntegrationLogCriteria() {}

    public IntegrationLogCriteria(IntegrationLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sourceApp = other.optionalSourceApp().map(AppTypeFilter::copy).orElse(null);
        this.targetApp = other.optionalTargetApp().map(AppTypeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(IntegrationStatusFilter::copy).orElse(null);
        this.retries = other.optionalRetries().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public IntegrationLogCriteria copy() {
        return new IntegrationLogCriteria(this);
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

    public AppTypeFilter getSourceApp() {
        return sourceApp;
    }

    public Optional<AppTypeFilter> optionalSourceApp() {
        return Optional.ofNullable(sourceApp);
    }

    public AppTypeFilter sourceApp() {
        if (sourceApp == null) {
            setSourceApp(new AppTypeFilter());
        }
        return sourceApp;
    }

    public void setSourceApp(AppTypeFilter sourceApp) {
        this.sourceApp = sourceApp;
    }

    public AppTypeFilter getTargetApp() {
        return targetApp;
    }

    public Optional<AppTypeFilter> optionalTargetApp() {
        return Optional.ofNullable(targetApp);
    }

    public AppTypeFilter targetApp() {
        if (targetApp == null) {
            setTargetApp(new AppTypeFilter());
        }
        return targetApp;
    }

    public void setTargetApp(AppTypeFilter targetApp) {
        this.targetApp = targetApp;
    }

    public IntegrationStatusFilter getStatus() {
        return status;
    }

    public Optional<IntegrationStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public IntegrationStatusFilter status() {
        if (status == null) {
            setStatus(new IntegrationStatusFilter());
        }
        return status;
    }

    public void setStatus(IntegrationStatusFilter status) {
        this.status = status;
    }

    public IntegerFilter getRetries() {
        return retries;
    }

    public Optional<IntegerFilter> optionalRetries() {
        return Optional.ofNullable(retries);
    }

    public IntegerFilter retries() {
        if (retries == null) {
            setRetries(new IntegerFilter());
        }
        return retries;
    }

    public void setRetries(IntegerFilter retries) {
        this.retries = retries;
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
        final IntegrationLogCriteria that = (IntegrationLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sourceApp, that.sourceApp) &&
            Objects.equals(targetApp, that.targetApp) &&
            Objects.equals(status, that.status) &&
            Objects.equals(retries, that.retries) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceApp, targetApp, status, retries, createdAt, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrationLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSourceApp().map(f -> "sourceApp=" + f + ", ").orElse("") +
            optionalTargetApp().map(f -> "targetApp=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRetries().map(f -> "retries=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
