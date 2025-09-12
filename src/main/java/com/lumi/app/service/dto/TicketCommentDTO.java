package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.Visibility;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.TicketComment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCommentDTO implements Serializable {

    private Long id;

    @NotNull
    private Long ticketId;

    @NotNull
    private Long authorId;

    @Lob
    private String body;

    @NotNull
    private Visibility visibility;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketCommentDTO)) {
            return false;
        }

        TicketCommentDTO ticketCommentDTO = (TicketCommentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketCommentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCommentDTO{" +
            "id=" + getId() +
            ", ticketId=" + getTicketId() +
            ", authorId=" + getAuthorId() +
            ", body='" + getBody() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
