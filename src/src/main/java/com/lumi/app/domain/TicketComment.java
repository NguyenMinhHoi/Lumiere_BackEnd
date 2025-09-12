package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TicketComment.
 */
@Entity
@Table(name = "ticket_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ticketcomment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Lob
    @Column(name = "body", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String body;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Visibility visibility;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TicketComment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return this.ticketId;
    }

    public TicketComment ticketId(Long ticketId) {
        this.setTicketId(ticketId);
        return this;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public TicketComment authorId(Long authorId) {
        this.setAuthorId(authorId);
        return this;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getBody() {
        return this.body;
    }

    public TicketComment body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public TicketComment visibility(Visibility visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public TicketComment createdAt(Instant createdAt) {
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
        if (!(o instanceof TicketComment)) {
            return false;
        }
        return getId() != null && getId().equals(((TicketComment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketComment{" +
            "id=" + getId() +
            ", ticketId=" + getTicketId() +
            ", authorId=" + getAuthorId() +
            ", body='" + getBody() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
