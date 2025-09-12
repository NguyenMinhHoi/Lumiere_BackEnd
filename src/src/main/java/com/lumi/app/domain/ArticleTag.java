package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ArticleTag.
 */
@Entity
@Table(name = "article_tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "articletag")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @NotNull
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ArticleTag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return this.articleId;
    }

    public ArticleTag articleId(Long articleId) {
        this.setArticleId(articleId);
        return this;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getTagId() {
        return this.tagId;
    }

    public ArticleTag tagId(Long tagId) {
        this.setTagId(tagId);
        return this;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleTag)) {
            return false;
        }
        return getId() != null && getId().equals(((ArticleTag) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArticleTag{" +
            "id=" + getId() +
            ", articleId=" + getArticleId() +
            ", tagId=" + getTagId() +
            "}";
    }
}
