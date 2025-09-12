package com.lumi.app.service.criteria;

import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Criteria class for the {@link com.lumi.app.domain.KnowledgeArticle} entity. This class is used
 * in {@link com.lumi.app.web.rest.KnowledgeArticleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /knowledge-articles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeArticleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter categoryId;

    private StringFilter title;

    private BooleanFilter published;

    private LongFilter views;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public KnowledgeArticleCriteria() {}

    public KnowledgeArticleCriteria(KnowledgeArticleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.published = other.optionalPublished().map(BooleanFilter::copy).orElse(null);
        this.views = other.optionalViews().map(LongFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public KnowledgeArticleCriteria copy() {
        return new KnowledgeArticleCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public BooleanFilter getPublished() {
        return published;
    }

    public Optional<BooleanFilter> optionalPublished() {
        return Optional.ofNullable(published);
    }

    public BooleanFilter published() {
        if (published == null) {
            setPublished(new BooleanFilter());
        }
        return published;
    }

    public void setPublished(BooleanFilter published) {
        this.published = published;
    }

    public LongFilter getViews() {
        return views;
    }

    public Optional<LongFilter> optionalViews() {
        return Optional.ofNullable(views);
    }

    public LongFilter views() {
        if (views == null) {
            setViews(new LongFilter());
        }
        return views;
    }

    public void setViews(LongFilter views) {
        this.views = views;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KnowledgeArticleCriteria that = (KnowledgeArticleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(title, that.title) &&
            Objects.equals(published, that.published) &&
            Objects.equals(views, that.views) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryId, title, published, views, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeArticleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalPublished().map(f -> "published=" + f + ", ").orElse("") +
            optionalViews().map(f -> "views=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
