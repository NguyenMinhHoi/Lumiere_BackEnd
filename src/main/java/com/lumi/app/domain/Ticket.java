package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.ChannelType;
import com.lumi.app.domain.enumeration.Priority;
import com.lumi.app.domain.enumeration.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ticket")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "sla_plan_id")
    private Long slaPlanId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "assignee_employee_id")
    private Long assigneeEmployeeId;

    @NotNull
    @Size(min = 6, max = 32)
    @Column(name = "code", length = 32, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @NotNull
    @Size(min = 3, max = 200)
    @Column(name = "subject", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subject;

    @Lob
    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TicketStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Priority priority;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ChannelType channel;

    @NotNull
    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "first_response_at")
    private Instant firstResponseAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "sla_due_at")
    private Instant slaDueAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ticket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public Ticket customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSlaPlanId() {
        return this.slaPlanId;
    }

    public Ticket slaPlanId(Long slaPlanId) {
        this.setSlaPlanId(slaPlanId);
        return this;
    }

    public void setSlaPlanId(Long slaPlanId) {
        this.slaPlanId = slaPlanId;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public Ticket orderId(Long orderId) {
        this.setOrderId(orderId);
        return this;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getAssigneeEmployeeId() {
        return this.assigneeEmployeeId;
    }

    public Ticket assigneeEmployeeId(Long assigneeEmployeeId) {
        this.setAssigneeEmployeeId(assigneeEmployeeId);
        return this;
    }

    public void setAssigneeEmployeeId(Long assigneeEmployeeId) {
        this.assigneeEmployeeId = assigneeEmployeeId;
    }

    public String getCode() {
        return this.code;
    }

    public Ticket code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubject() {
        return this.subject;
    }

    public Ticket subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return this.description;
    }

    public Ticket description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return this.status;
    }

    public Ticket status(TicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public Ticket priority(Priority priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ChannelType getChannel() {
        return this.channel;
    }

    public Ticket channel(ChannelType channel) {
        this.setChannel(channel);
        return this;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public Instant getOpenedAt() {
        return this.openedAt;
    }

    public Ticket openedAt(Instant openedAt) {
        this.setOpenedAt(openedAt);
        return this;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getFirstResponseAt() {
        return this.firstResponseAt;
    }

    public Ticket firstResponseAt(Instant firstResponseAt) {
        this.setFirstResponseAt(firstResponseAt);
        return this;
    }

    public void setFirstResponseAt(Instant firstResponseAt) {
        this.firstResponseAt = firstResponseAt;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public Ticket resolvedAt(Instant resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Instant getSlaDueAt() {
        return this.slaDueAt;
    }

    public Ticket slaDueAt(Instant slaDueAt) {
        this.setSlaDueAt(slaDueAt);
        return this;
    }

    public void setSlaDueAt(Instant slaDueAt) {
        this.slaDueAt = slaDueAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return getId() != null && getId().equals(((Ticket) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ticket{" +
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
