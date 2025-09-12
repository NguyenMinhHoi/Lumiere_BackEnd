package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SurveyResponse.
 */
@Entity
@Table(name = "survey_response")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "surveyresponse")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "ticket_id")
    private Long ticketId;

    @NotNull
    @Column(name = "responded_at", nullable = false)
    private Instant respondedAt;

    @Column(name = "score")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer score;

    @Lob
    @Column(name = "comment")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String comment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SurveyResponse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public SurveyResponse surveyId(Long surveyId) {
        this.setSurveyId(surveyId);
        return this;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public SurveyResponse customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getTicketId() {
        return this.ticketId;
    }

    public SurveyResponse ticketId(Long ticketId) {
        this.setTicketId(ticketId);
        return this;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Instant getRespondedAt() {
        return this.respondedAt;
    }

    public SurveyResponse respondedAt(Instant respondedAt) {
        this.setRespondedAt(respondedAt);
        return this;
    }

    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }

    public Integer getScore() {
        return this.score;
    }

    public SurveyResponse score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return this.comment;
    }

    public SurveyResponse comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyResponse)) {
            return false;
        }
        return getId() != null && getId().equals(((SurveyResponse) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyResponse{" +
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
