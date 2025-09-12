package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.QuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.SurveyQuestion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyQuestionDTO implements Serializable {

    private Long id;

    @NotNull
    private Long surveyId;

    @NotNull
    @Size(min = 3, max = 300)
    private String text;

    @NotNull
    private QuestionType questionType;

    private Integer scaleMin;

    private Integer scaleMax;

    @NotNull
    private Boolean isNeed;

    @NotNull
    @Min(value = 1)
    private Integer orderNo;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getScaleMin() {
        return scaleMin;
    }

    public void setScaleMin(Integer scaleMin) {
        this.scaleMin = scaleMin;
    }

    public Integer getScaleMax() {
        return scaleMax;
    }

    public void setScaleMax(Integer scaleMax) {
        this.scaleMax = scaleMax;
    }

    public Boolean getIsNeed() {
        return isNeed;
    }

    public void setIsNeed(Boolean isNeed) {
        this.isNeed = isNeed;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyQuestionDTO)) {
            return false;
        }

        SurveyQuestionDTO surveyQuestionDTO = (SurveyQuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, surveyQuestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyQuestionDTO{" +
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
