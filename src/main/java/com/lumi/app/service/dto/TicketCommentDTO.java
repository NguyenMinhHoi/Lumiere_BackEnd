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

    @Lob
    private String content;

    @NotNull
    private Visibility visibility;

    @NotNull
    private Instant createdAt;

    private TicketDTO ticket;

    private UserDTO author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return content;
    }

    public void setBody(String body) {
        this.content = body;
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

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
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
            ", body='" + getBody() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", ticket=" + getTicket() +
            ", author=" + getAuthor() +
            "}";
    }
}
