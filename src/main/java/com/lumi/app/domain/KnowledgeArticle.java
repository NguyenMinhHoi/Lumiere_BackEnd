package com.lumi.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A KnowledgeArticle.
 */
@Entity
@Table(name = "knowledge_article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "knowledgearticle")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5, max = 200)
    @Column(name = "title", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @Column(name = "content", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String content;

    @NotNull
    @Column(name = "published", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean published;

    @NotNull
    @Min(value = 0L)
    @Column(name = "views", nullable = false)
    private Long views;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private KnowledgeCategory category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_knowledge_article__tags",
        joinColumns = @JoinColumn(name = "knowledge_article_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tickets", "articles" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public KnowledgeArticle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public KnowledgeArticle title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public KnowledgeArticle content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public KnowledgeArticle published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Long getViews() {
        return this.views;
    }

    public KnowledgeArticle views(Long views) {
        this.setViews(views);
        return this;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public KnowledgeArticle updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public KnowledgeCategory getCategory() {
        return this.category;
    }

    public void setCategory(KnowledgeCategory knowledgeCategory) {
        this.category = knowledgeCategory;
    }

    public KnowledgeArticle category(KnowledgeCategory knowledgeCategory) {
        this.setCategory(knowledgeCategory);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public KnowledgeArticle tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public KnowledgeArticle addTags(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public KnowledgeArticle removeTags(Tag tag) {
        this.tags.remove(tag);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KnowledgeArticle)) {
            return false;
        }
        return getId() != null && getId().equals(((KnowledgeArticle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeArticle{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", published='" + getPublished() + "'" +
            ", views=" + getViews() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
