package com.lumi.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.KnowledgeArticle} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeArticleDTO implements Serializable {

    private Long id;

    private Long categoryId;

    @NotNull
    @Size(min = 5, max = 200)
    private String title;

    @Lob
    private String content;

    @NotNull
    private Boolean published;

    @NotNull
    @Min(value = 0L)
    private Long views;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KnowledgeArticleDTO)) {
            return false;
        }

        KnowledgeArticleDTO knowledgeArticleDTO = (KnowledgeArticleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, knowledgeArticleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeArticleDTO{" +
            "id=" + getId() +
            ", categoryId=" + getCategoryId() +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", published='" + getPublished() + "'" +
            ", views=" + getViews() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
