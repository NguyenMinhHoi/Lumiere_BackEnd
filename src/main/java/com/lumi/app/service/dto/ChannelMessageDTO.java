package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.MessageDirection;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.ChannelMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChannelMessageDTO implements Serializable {

    private Long id;

    @NotNull
    private Long ticketId;

    private Long authorId;

    @NotNull
    private MessageDirection direction;

    @Lob
    private String content;

    @NotNull
    private Instant sentAt;

    @Size(max = 128)
    private String externalMessageId;

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

    public MessageDirection getDirection() {
        return direction;
    }

    public void setDirection(MessageDirection direction) {
        this.direction = direction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getExternalMessageId() {
        return externalMessageId;
    }

    public void setExternalMessageId(String externalMessageId) {
        this.externalMessageId = externalMessageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChannelMessageDTO)) {
            return false;
        }

        ChannelMessageDTO channelMessageDTO = (ChannelMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, channelMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChannelMessageDTO{" +
            "id=" + getId() +
            ", ticketId=" + getTicketId() +
            ", authorId=" + getAuthorId() +
            ", direction='" + getDirection() + "'" +
            ", content='" + getContent() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", externalMessageId='" + getExternalMessageId() + "'" +
            "}";
    }
}
