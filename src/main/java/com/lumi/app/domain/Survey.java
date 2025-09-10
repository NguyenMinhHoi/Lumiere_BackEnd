package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.SurveyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Survey.
 */
@Entity
@Table(name = "survey")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "survey")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Survey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "survey_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private SurveyType surveyType;

    @NotNull
    @Size(min = 3, max = 200)
    @Column(name = "title", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "due_at")
    private Instant dueAt;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Survey id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurveyType getSurveyType() {
        return this.surveyType;
    }

    public Survey surveyType(SurveyType surveyType) {
        this.setSurveyType(surveyType);
        return this;
    }

    public void setSurveyType(SurveyType surveyType) {
        this.surveyType = surveyType;
    }

    public String getTitle() {
        return this.title;
    }

    public Survey title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public Survey sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getDueAt() {
        return this.dueAt;
    }

    public Survey dueAt(Instant dueAt) {
        this.setDueAt(dueAt);
        return this;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Survey isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Survey customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Survey)) {
            return false;
        }
        return getId() != null && getId().equals(((Survey) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Survey{" +
            "id=" + getId() +
            ", surveyType='" + getSurveyType() + "'" +
            ", title='" + getTitle() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", dueAt='" + getDueAt() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
