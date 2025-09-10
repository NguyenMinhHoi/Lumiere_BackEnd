package com.lumi.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.app.domain.enumeration.ChannelType;
import com.lumi.app.domain.enumeration.Priority;
import com.lumi.app.domain.enumeration.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
    @Size(min = 6, max = 32)
    @Column(name = "code", length = 32, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @NotNull
    @Size(min = 3, max = 200)
    @Column(name = "subject", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subject;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    private SlaPlan slaPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Orders order;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_ticket__tags", joinColumns = @JoinColumn(name = "ticket_id"), inverseJoinColumns = @JoinColumn(name = "tags_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tickets", "articles" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

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

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Ticket customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public User getAssignee() {
        return this.assignee;
    }

    public void setAssignee(User user) {
        this.assignee = user;
    }

    public Ticket assignee(User user) {
        this.setAssignee(user);
        return this;
    }

    public SlaPlan getSlaPlan() {
        return this.slaPlan;
    }

    public void setSlaPlan(SlaPlan slaPlan) {
        this.slaPlan = slaPlan;
    }

    public Ticket slaPlan(SlaPlan slaPlan) {
        this.setSlaPlan(slaPlan);
        return this;
    }

    public Orders getOrder() {
        return this.order;
    }

    public void setOrder(Orders orders) {
        this.order = orders;
    }

    public Ticket order(Orders orders) {
        this.setOrder(orders);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Ticket tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Ticket addTags(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Ticket removeTags(Tag tag) {
        this.tags.remove(tag);
        return this;
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
