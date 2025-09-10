package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.DeliveryChannel;
import com.lumi.app.domain.enumeration.NotificationType;
import com.lumi.app.domain.enumeration.SendStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.Notification} entity. This class is used
 * in {@link com.lumi.app.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering NotificationType
     */
    public static class NotificationTypeFilter extends Filter<NotificationType> {

        public NotificationTypeFilter() {}

        public NotificationTypeFilter(NotificationTypeFilter filter) {
            super(filter);
        }

        @Override
        public NotificationTypeFilter copy() {
            return new NotificationTypeFilter(this);
        }
    }

    /**
     * Class for filtering DeliveryChannel
     */
    public static class DeliveryChannelFilter extends Filter<DeliveryChannel> {

        public DeliveryChannelFilter() {}

        public DeliveryChannelFilter(DeliveryChannelFilter filter) {
            super(filter);
        }

        @Override
        public DeliveryChannelFilter copy() {
            return new DeliveryChannelFilter(this);
        }
    }

    /**
     * Class for filtering SendStatus
     */
    public static class SendStatusFilter extends Filter<SendStatus> {

        public SendStatusFilter() {}

        public SendStatusFilter(SendStatusFilter filter) {
            super(filter);
        }

        @Override
        public SendStatusFilter copy() {
            return new SendStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private NotificationTypeFilter type;

    private DeliveryChannelFilter channel;

    private StringFilter subject;

    private SendStatusFilter sendStatus;

    private IntegerFilter retryCount;

    private InstantFilter lastTriedAt;

    private InstantFilter createdAt;

    private LongFilter ticketId;

    private LongFilter customerId;

    private LongFilter surveyId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(NotificationTypeFilter::copy).orElse(null);
        this.channel = other.optionalChannel().map(DeliveryChannelFilter::copy).orElse(null);
        this.subject = other.optionalSubject().map(StringFilter::copy).orElse(null);
        this.sendStatus = other.optionalSendStatus().map(SendStatusFilter::copy).orElse(null);
        this.retryCount = other.optionalRetryCount().map(IntegerFilter::copy).orElse(null);
        this.lastTriedAt = other.optionalLastTriedAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.ticketId = other.optionalTicketId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.surveyId = other.optionalSurveyId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
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

    public NotificationTypeFilter getType() {
        return type;
    }

    public Optional<NotificationTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public NotificationTypeFilter type() {
        if (type == null) {
            setType(new NotificationTypeFilter());
        }
        return type;
    }

    public void setType(NotificationTypeFilter type) {
        this.type = type;
    }

    public DeliveryChannelFilter getChannel() {
        return channel;
    }

    public Optional<DeliveryChannelFilter> optionalChannel() {
        return Optional.ofNullable(channel);
    }

    public DeliveryChannelFilter channel() {
        if (channel == null) {
            setChannel(new DeliveryChannelFilter());
        }
        return channel;
    }

    public void setChannel(DeliveryChannelFilter channel) {
        this.channel = channel;
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

    public SendStatusFilter getSendStatus() {
        return sendStatus;
    }

    public Optional<SendStatusFilter> optionalSendStatus() {
        return Optional.ofNullable(sendStatus);
    }

    public SendStatusFilter sendStatus() {
        if (sendStatus == null) {
            setSendStatus(new SendStatusFilter());
        }
        return sendStatus;
    }

    public void setSendStatus(SendStatusFilter sendStatus) {
        this.sendStatus = sendStatus;
    }

    public IntegerFilter getRetryCount() {
        return retryCount;
    }

    public Optional<IntegerFilter> optionalRetryCount() {
        return Optional.ofNullable(retryCount);
    }

    public IntegerFilter retryCount() {
        if (retryCount == null) {
            setRetryCount(new IntegerFilter());
        }
        return retryCount;
    }

    public void setRetryCount(IntegerFilter retryCount) {
        this.retryCount = retryCount;
    }

    public InstantFilter getLastTriedAt() {
        return lastTriedAt;
    }

    public Optional<InstantFilter> optionalLastTriedAt() {
        return Optional.ofNullable(lastTriedAt);
    }

    public InstantFilter lastTriedAt() {
        if (lastTriedAt == null) {
            setLastTriedAt(new InstantFilter());
        }
        return lastTriedAt;
    }

    public void setLastTriedAt(InstantFilter lastTriedAt) {
        this.lastTriedAt = lastTriedAt;
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

    public LongFilter getTicketId() {
        return ticketId;
    }

    public Optional<LongFilter> optionalTicketId() {
        return Optional.ofNullable(ticketId);
    }

    public LongFilter ticketId() {
        if (ticketId == null) {
            setTicketId(new LongFilter());
        }
        return ticketId;
    }

    public void setTicketId(LongFilter ticketId) {
        this.ticketId = ticketId;
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

    public LongFilter getSurveyId() {
        return surveyId;
    }

    public Optional<LongFilter> optionalSurveyId() {
        return Optional.ofNullable(surveyId);
    }

    public LongFilter surveyId() {
        if (surveyId == null) {
            setSurveyId(new LongFilter());
        }
        return surveyId;
    }

    public void setSurveyId(LongFilter surveyId) {
        this.surveyId = surveyId;
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
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(channel, that.channel) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(sendStatus, that.sendStatus) &&
            Objects.equals(retryCount, that.retryCount) &&
            Objects.equals(lastTriedAt, that.lastTriedAt) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(ticketId, that.ticketId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(surveyId, that.surveyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            type,
            channel,
            subject,
            sendStatus,
            retryCount,
            lastTriedAt,
            createdAt,
            ticketId,
            customerId,
            surveyId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalChannel().map(f -> "channel=" + f + ", ").orElse("") +
            optionalSubject().map(f -> "subject=" + f + ", ").orElse("") +
            optionalSendStatus().map(f -> "sendStatus=" + f + ", ").orElse("") +
            optionalRetryCount().map(f -> "retryCount=" + f + ", ").orElse("") +
            optionalLastTriedAt().map(f -> "lastTriedAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalTicketId().map(f -> "ticketId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalSurveyId().map(f -> "surveyId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
