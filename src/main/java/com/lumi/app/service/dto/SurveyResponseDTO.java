package com.lumi.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.SurveyResponse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyResponseDTO implements Serializable {

    private Long id;

    @NotNull
    private Long surveyId;

    private Long customerId;

    private Long ticketId;

    @NotNull
    private Instant respondedAt;

    private Integer score;

    @Lob
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyResponseDTO)) {
            return false;
        }

        SurveyResponseDTO surveyResponseDTO = (SurveyResponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, surveyResponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyResponseDTO{" +
            "id=" + getId() +
            ", surveyId=" + getSurveyId() +
            ", customerId=" + getCustomerId() +
            ", ticketId=" + getTicketId() +
            ", respondedAt='" + getRespondedAt() + "'" +
            ", score=" + getScore() +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
