package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SurveyQuestion.
 */
@Entity
@Table(name = "survey_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "surveyquestion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @NotNull
    @Size(min = 3, max = 300)
    @Column(name = "text", length = 300, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String text;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QuestionType questionType;

    @Column(name = "scale_min")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer scaleMin;

    @Column(name = "scale_max")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer scaleMax;

    @NotNull
    @Column(name = "is_need", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isNeed;

    @NotNull
    @Min(value = 1)
    @Column(name = "order_no", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer orderNo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SurveyQuestion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public SurveyQuestion surveyId(Long surveyId) {
        this.setSurveyId(surveyId);
        return this;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getText() {
        return this.text;
    }

    public SurveyQuestion text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getQuestionType() {
        return this.questionType;
    }

    public SurveyQuestion questionType(QuestionType questionType) {
        this.setQuestionType(questionType);
        return this;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getScaleMin() {
        return this.scaleMin;
    }

    public SurveyQuestion scaleMin(Integer scaleMin) {
        this.setScaleMin(scaleMin);
        return this;
    }

    public void setScaleMin(Integer scaleMin) {
        this.scaleMin = scaleMin;
    }

    public Integer getScaleMax() {
        return this.scaleMax;
    }

    public SurveyQuestion scaleMax(Integer scaleMax) {
        this.setScaleMax(scaleMax);
        return this;
    }

    public void setScaleMax(Integer scaleMax) {
        this.scaleMax = scaleMax;
    }

    public Boolean getIsNeed() {
        return this.isNeed;
    }

    public SurveyQuestion isNeed(Boolean isNeed) {
        this.setIsNeed(isNeed);
        return this;
    }

    public void setIsNeed(Boolean isNeed) {
        this.isNeed = isNeed;
    }

    public Integer getOrderNo() {
        return this.orderNo;
    }

    public SurveyQuestion orderNo(Integer orderNo) {
        this.setOrderNo(orderNo);
        return this;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyQuestion)) {
            return false;
        }
        return getId() != null && getId().equals(((SurveyQuestion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyQuestion{" +
            "id=" + getId() +
            ", surveyId=" + getSurveyId() +
            ", text='" + getText() + "'" +
            ", questionType='" + getQuestionType() + "'" +
            ", scaleMin=" + getScaleMin() +
            ", scaleMax=" + getScaleMax() +
            ", isNeed='" + getIsNeed() + "'" +
            ", orderNo=" + getOrderNo() +
            "}";
    }
}
