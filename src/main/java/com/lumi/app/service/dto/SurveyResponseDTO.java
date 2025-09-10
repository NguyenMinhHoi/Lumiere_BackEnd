package com.lumi.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
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
    private Instant respondedAt;

    private Integer score;

    @Lob
    private String comment;

    private SurveyDTO survey;

    private CustomerDTO customer;

    private TicketDTO ticket;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SurveyDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyDTO survey) {
        this.survey = survey;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
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
            ", respondedAt='" + getRespondedAt() + "'" +
            ", score=" + getScore() +
            ", comment='" + getComment() + "'" +
            ", survey=" + getSurvey() +
            ", customer=" + getCustomer() +
            ", ticket=" + getTicket() +
            "}";
    }
}
