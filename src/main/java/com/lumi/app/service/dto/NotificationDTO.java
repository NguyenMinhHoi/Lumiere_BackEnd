package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.DeliveryChannel;
import com.lumi.app.domain.enumeration.NotificationType;
import com.lumi.app.domain.enumeration.SendStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.Notification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationDTO implements Serializable {

    private Long id;

    @NotNull
    private NotificationType type;

    @NotNull
    private DeliveryChannel channel;

    @Size(max = 200)
    private String subject;

    @Lob
    private String payload;

    @NotNull
    private SendStatus sendStatus;

    @NotNull
    @Min(value = 0)
    private Integer retryCount;

    private Instant lastTriedAt;

    @NotNull
    private Instant createdAt;

    private TicketDTO ticket;

    private CustomerDTO customer;

    private SurveyDTO survey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public DeliveryChannel getChannel() {
        return channel;
    }

    public void setChannel(DeliveryChannel channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getLastTriedAt() {
        return lastTriedAt;
    }

    public void setLastTriedAt(Instant lastTriedAt) {
        this.lastTriedAt = lastTriedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public SurveyDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyDTO survey) {
        this.survey = survey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDTO)) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", channel='" + getChannel() + "'" +
            ", subject='" + getSubject() + "'" +
            ", payload='" + getPayload() + "'" +
            ", sendStatus='" + getSendStatus() + "'" +
            ", retryCount=" + getRetryCount() +
            ", lastTriedAt='" + getLastTriedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", ticket=" + getTicket() +
            ", customer=" + getCustomer() +
            ", survey=" + getSurvey() +
            "}";
    }
}
