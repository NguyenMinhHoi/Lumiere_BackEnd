package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.ChannelType;
import com.lumi.app.domain.enumeration.Priority;
import com.lumi.app.domain.enumeration.TicketStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.Ticket} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDTO implements Serializable {

    private Long id;

    @NotNull
    private Long customerId;

    private Long slaPlanId;

    private Long orderId;

    private Long assigneeEmployeeId;

    @NotNull
    @Size(min = 6, max = 32)
    private String code;

    @NotNull
    @Size(min = 3, max = 200)
    private String subject;

    @Lob
    private String description;

    @NotNull
    private TicketStatus status;

    @NotNull
    private Priority priority;

    @NotNull
    private ChannelType channel;

    @NotNull
    private Instant openedAt;

    private Instant firstResponseAt;

    private Instant resolvedAt;

    private Instant slaDueAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSlaPlanId() {
        return slaPlanId;
    }

    public void setSlaPlanId(Long slaPlanId) {
        this.slaPlanId = slaPlanId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getAssigneeEmployeeId() {
        return assigneeEmployeeId;
    }

    public void setAssigneeEmployeeId(Long assigneeEmployeeId) {
        this.assigneeEmployeeId = assigneeEmployeeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ChannelType getChannel() {
        return channel;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getFirstResponseAt() {
        return firstResponseAt;
    }

    public void setFirstResponseAt(Instant firstResponseAt) {
        this.firstResponseAt = firstResponseAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Instant getSlaDueAt() {
        return slaDueAt;
    }

    public void setSlaDueAt(Instant slaDueAt) {
        this.slaDueAt = slaDueAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDTO)) {
            return false;
        }

        TicketDTO ticketDTO = (TicketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDTO{" +
            "id=" + getId() +
            ", customerId=" + getCustomerId() +
            ", slaPlanId=" + getSlaPlanId() +
            ", orderId=" + getOrderId() +
            ", assigneeEmployeeId=" + getAssigneeEmployeeId() +
            ", code='" + getCode() + "'" +
            ", subject='" + getSubject() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", priority='" + getPriority() + "'" +
            ", channel='" + getChannel() + "'" +
            ", openedAt='" + getOpenedAt() + "'" +
            ", firstResponseAt='" + getFirstResponseAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", slaDueAt='" + getSlaDueAt() + "'" +
            "}";
    }
}
