package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.DeliveryChannel;
import com.lumi.app.domain.enumeration.NotificationType;
import com.lumi.app.domain.enumeration.SendStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "notification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "survey_id")
    private Long surveyId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private DeliveryChannel channel;

    @Size(max = 200)
    @Column(name = "subject", length = 200)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subject;

    @Lob
    @Column(name = "payload", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String payload;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "send_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private SendStatus sendStatus;

    @NotNull
    @Min(value = 0)
    @Column(name = "retry_count", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer retryCount;

    @Column(name = "last_tried_at")
    private Instant lastTriedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return this.ticketId;
    }

    public Notification ticketId(Long ticketId) {
        this.setTicketId(ticketId);
        return this;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public Notification customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public Notification surveyId(Long surveyId) {
        this.setSurveyId(surveyId);
        return this;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public NotificationType getType() {
        return this.type;
    }

    public Notification type(NotificationType type) {
        this.setType(type);
        return this;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public DeliveryChannel getChannel() {
        return this.channel;
    }

    public Notification channel(DeliveryChannel channel) {
        this.setChannel(channel);
        return this;
    }

    public void setChannel(DeliveryChannel channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return this.subject;
    }

    public Notification subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPayload() {
        return this.payload;
    }

    public Notification payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public SendStatus getSendStatus() {
        return this.sendStatus;
    }

    public Notification sendStatus(SendStatus sendStatus) {
        this.setSendStatus(sendStatus);
        return this;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public Notification retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getLastTriedAt() {
        return this.lastTriedAt;
    }

    public Notification lastTriedAt(Instant lastTriedAt) {
        this.setLastTriedAt(lastTriedAt);
        return this;
    }

    public void setLastTriedAt(Instant lastTriedAt) {
        this.lastTriedAt = lastTriedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Notification createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return getId() != null && getId().equals(((Notification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", ticketId=" + getTicketId() +
            ", customerId=" + getCustomerId() +
            ", surveyId=" + getSurveyId() +
            ", type='" + getType() + "'" +
            ", channel='" + getChannel() + "'" +
            ", subject='" + getSubject() + "'" +
            ", payload='" + getPayload() + "'" +
            ", sendStatus='" + getSendStatus() + "'" +
            ", retryCount=" + getRetryCount() +
            ", lastTriedAt='" + getLastTriedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
