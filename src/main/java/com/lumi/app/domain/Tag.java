package com.lumi.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tag")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 64)
    @Column(name = "name", length = 64, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "assignee", "slaPlan", "order", "tags" }, allowSetters = true)
    private Set<Ticket> tickets = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "category", "tags" }, allowSetters = true)
    private Set<KnowledgeArticle> articles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Tag name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Ticket> getTickets() {
        return this.tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        if (this.tickets != null) {
            this.tickets.forEach(i -> i.removeTags(this));
        }
        if (tickets != null) {
            tickets.forEach(i -> i.addTags(this));
        }
        this.tickets = tickets;
    }

    public Tag tickets(Set<Ticket> tickets) {
        this.setTickets(tickets);
        return this;
    }

    public Tag addTickets(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.getTags().add(this);
        return this;
    }

    public Tag removeTickets(Ticket ticket) {
        this.tickets.remove(ticket);
        ticket.getTags().remove(this);
        return this;
    }

    public Set<KnowledgeArticle> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<KnowledgeArticle> knowledgeArticles) {
        if (this.articles != null) {
            this.articles.forEach(i -> i.removeTags(this));
        }
        if (knowledgeArticles != null) {
            knowledgeArticles.forEach(i -> i.addTags(this));
        }
        this.articles = knowledgeArticles;
    }

    public Tag articles(Set<KnowledgeArticle> knowledgeArticles) {
        this.setArticles(knowledgeArticles);
        return this;
    }

    public Tag addArticles(KnowledgeArticle knowledgeArticle) {
        this.articles.add(knowledgeArticle);
        knowledgeArticle.getTags().add(this);
        return this;
    }

    public Tag removeArticles(KnowledgeArticle knowledgeArticle) {
        this.articles.remove(knowledgeArticle);
        knowledgeArticle.getTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        return getId() != null && getId().equals(((Tag) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
