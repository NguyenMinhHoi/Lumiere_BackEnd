package com.lumi.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.app.domain.enumeration.MessageDirection;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ChannelMessage.
 */
@Entity
@Table(name = "channel_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "channelmessage")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChannelMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private MessageDirection direction;

    @Column(name = "content", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String content;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Size(max = 128)
    @Column(name = "external_message_id", length = 128)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String externalMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "assignee", "slaPlan", "order", "tags" }, allowSetters = true)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChannelMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageDirection getDirection() {
        return this.direction;
    }

    public ChannelMessage direction(MessageDirection direction) {
        this.setDirection(direction);
        return this;
    }

    public void setDirection(MessageDirection direction) {
        this.direction = direction;
    }

    public String getContent() {
        return this.content;
    }

    public ChannelMessage content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public ChannelMessage sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getExternalMessageId() {
        return this.externalMessageId;
    }

    public ChannelMessage externalMessageId(String externalMessageId) {
        this.setExternalMessageId(externalMessageId);
        return this;
    }

    public void setExternalMessageId(String externalMessageId) {
        this.externalMessageId = externalMessageId;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public ChannelMessage ticket(Ticket ticket) {
        this.setTicket(ticket);
        return this;
    }

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public ChannelMessage author(User user) {
        this.setAuthor(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChannelMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((ChannelMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChannelMessage{" +
            "id=" + getId() +
            ", direction='" + getDirection() + "'" +
            ", content='" + getContent() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", externalMessageId='" + getExternalMessageId() + "'" +
            "}";
    }
}
