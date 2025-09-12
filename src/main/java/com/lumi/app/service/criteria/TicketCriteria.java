package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.ChannelType;
import com.lumi.app.domain.enumeration.Priority;
import com.lumi.app.domain.enumeration.TicketStatus;
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
 * Criteria class for the {@link com.lumi.app.domain.Ticket} entity. This class is used
 * in {@link com.lumi.app.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TicketStatus
     */
    public static class TicketStatusFilter extends Filter<TicketStatus> {

        public TicketStatusFilter() {}

        public TicketStatusFilter(TicketStatusFilter filter) {
            super(filter);
        }

        @Override
        public TicketStatusFilter copy() {
            return new TicketStatusFilter(this);
        }
    }

    /**
     * Class for filtering Priority
     */
    public static class PriorityFilter extends Filter<Priority> {

        public PriorityFilter() {}

        public PriorityFilter(PriorityFilter filter) {
            super(filter);
        }

        @Override
        public PriorityFilter copy() {
            return new PriorityFilter(this);
        }
    }

    /**
     * Class for filtering ChannelType
     */
    public static class ChannelTypeFilter extends Filter<ChannelType> {

        public ChannelTypeFilter() {}

        public ChannelTypeFilter(ChannelTypeFilter filter) {
            super(filter);
        }

        @Override
        public ChannelTypeFilter copy() {
            return new ChannelTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter customerId;

    private LongFilter slaPlanId;

    private LongFilter orderId;

    private LongFilter assigneeEmployeeId;

    private StringFilter code;

    private StringFilter subject;

    private TicketStatusFilter status;

    private PriorityFilter priority;

    private ChannelTypeFilter channel;

    private InstantFilter openedAt;

    private InstantFilter firstResponseAt;

    private InstantFilter resolvedAt;

    private InstantFilter slaDueAt;

    private Boolean distinct;

    public TicketCriteria() {}

    public TicketCriteria(TicketCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.slaPlanId = other.optionalSlaPlanId().map(LongFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.assigneeEmployeeId = other.optionalAssigneeEmployeeId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.subject = other.optionalSubject().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TicketStatusFilter::copy).orElse(null);
        this.priority = other.optionalPriority().map(PriorityFilter::copy).orElse(null);
        this.channel = other.optionalChannel().map(ChannelTypeFilter::copy).orElse(null);
        this.openedAt = other.optionalOpenedAt().map(InstantFilter::copy).orElse(null);
        this.firstResponseAt = other.optionalFirstResponseAt().map(InstantFilter::copy).orElse(null);
        this.resolvedAt = other.optionalResolvedAt().map(InstantFilter::copy).orElse(null);
        this.slaDueAt = other.optionalSlaDueAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
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

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getSlaPlanId() {
        return slaPlanId;
    }

    public Optional<LongFilter> optionalSlaPlanId() {
        return Optional.ofNullable(slaPlanId);
    }

    public LongFilter slaPlanId() {
        if (slaPlanId == null) {
            setSlaPlanId(new LongFilter());
        }
        return slaPlanId;
    }

    public void setSlaPlanId(LongFilter slaPlanId) {
        this.slaPlanId = slaPlanId;
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

    public LongFilter getAssigneeEmployeeId() {
        return assigneeEmployeeId;
    }

    public Optional<LongFilter> optionalAssigneeEmployeeId() {
        return Optional.ofNullable(assigneeEmployeeId);
    }

    public LongFilter assigneeEmployeeId() {
        if (assigneeEmployeeId == null) {
            setAssigneeEmployeeId(new LongFilter());
        }
        return assigneeEmployeeId;
    }

    public void setAssigneeEmployeeId(LongFilter assigneeEmployeeId) {
        this.assigneeEmployeeId = assigneeEmployeeId;
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

    public StringFilter getSubject() {
        return subject;
    }

    public Optional<StringFilter> optionalSubject() {
        return Optional.ofNullable(subject);
    }

    public StringFilter subject() {
        if (subject == null) {
            setSubject(new StringFilter());
        }
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
    }

    public TicketStatusFilter getStatus() {
        return status;
    }

    public Optional<TicketStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TicketStatusFilter status() {
        if (status == null) {
            setStatus(new TicketStatusFilter());
        }
        return status;
    }

    public void setStatus(TicketStatusFilter status) {
        this.status = status;
    }

    public PriorityFilter getPriority() {
        return priority;
    }

    public Optional<PriorityFilter> optionalPriority() {
        return Optional.ofNullable(priority);
    }

    public PriorityFilter priority() {
        if (priority == null) {
            setPriority(new PriorityFilter());
        }
        return priority;
    }

    public void setPriority(PriorityFilter priority) {
        this.priority = priority;
    }

    public ChannelTypeFilter getChannel() {
        return channel;
    }

    public Optional<ChannelTypeFilter> optionalChannel() {
        return Optional.ofNullable(channel);
    }

    public ChannelTypeFilter channel() {
        if (channel == null) {
            setChannel(new ChannelTypeFilter());
        }
        return channel;
    }

    public void setChannel(ChannelTypeFilter channel) {
        this.channel = channel;
    }

    public InstantFilter getOpenedAt() {
        return openedAt;
    }

    public Optional<InstantFilter> optionalOpenedAt() {
        return Optional.ofNullable(openedAt);
    }

    public InstantFilter openedAt() {
        if (openedAt == null) {
            setOpenedAt(new InstantFilter());
        }
        return openedAt;
    }

    public void setOpenedAt(InstantFilter openedAt) {
        this.openedAt = openedAt;
    }

    public InstantFilter getFirstResponseAt() {
        return firstResponseAt;
    }

    public Optional<InstantFilter> optionalFirstResponseAt() {
        return Optional.ofNullable(firstResponseAt);
    }

    public InstantFilter firstResponseAt() {
        if (firstResponseAt == null) {
            setFirstResponseAt(new InstantFilter());
        }
        return firstResponseAt;
    }

    public void setFirstResponseAt(InstantFilter firstResponseAt) {
        this.firstResponseAt = firstResponseAt;
    }

    public InstantFilter getResolvedAt() {
        return resolvedAt;
    }

    public Optional<InstantFilter> optionalResolvedAt() {
        return Optional.ofNullable(resolvedAt);
    }

    public InstantFilter resolvedAt() {
        if (resolvedAt == null) {
            setResolvedAt(new InstantFilter());
        }
        return resolvedAt;
    }

    public void setResolvedAt(InstantFilter resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public InstantFilter getSlaDueAt() {
        return slaDueAt;
    }

    public Optional<InstantFilter> optionalSlaDueAt() {
        return Optional.ofNullable(slaDueAt);
    }

    public InstantFilter slaDueAt() {
        if (slaDueAt == null) {
            setSlaDueAt(new InstantFilter());
        }
        return slaDueAt;
    }

    public void setSlaDueAt(InstantFilter slaDueAt) {
        this.slaDueAt = slaDueAt;
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
        final TicketCriteria that = (TicketCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(slaPlanId, that.slaPlanId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(assigneeEmployeeId, that.assigneeEmployeeId) &&
            Objects.equals(code, that.code) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(status, that.status) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(channel, that.channel) &&
            Objects.equals(openedAt, that.openedAt) &&
            Objects.equals(firstResponseAt, that.firstResponseAt) &&
            Objects.equals(resolvedAt, that.resolvedAt) &&
            Objects.equals(slaDueAt, that.slaDueAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            customerId,
            slaPlanId,
            orderId,
            assigneeEmployeeId,
            code,
            subject,
            status,
            priority,
            channel,
            openedAt,
            firstResponseAt,
            resolvedAt,
            slaDueAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalSlaPlanId().map(f -> "slaPlanId=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalAssigneeEmployeeId().map(f -> "assigneeEmployeeId=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalSubject().map(f -> "subject=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPriority().map(f -> "priority=" + f + ", ").orElse("") +
            optionalChannel().map(f -> "channel=" + f + ", ").orElse("") +
            optionalOpenedAt().map(f -> "openedAt=" + f + ", ").orElse("") +
            optionalFirstResponseAt().map(f -> "firstResponseAt=" + f + ", ").orElse("") +
            optionalResolvedAt().map(f -> "resolvedAt=" + f + ", ").orElse("") +
            optionalSlaDueAt().map(f -> "slaDueAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
