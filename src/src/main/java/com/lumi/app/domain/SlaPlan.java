package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SlaPlan.
 */
@Entity
@Table(name = "sla_plan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "slaplan")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SlaPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 128)
    @Column(name = "name", length = 128, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Min(value = 1)
    @Column(name = "first_response_mins", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer firstResponseMins;

    @NotNull
    @Min(value = 5)
    @Column(name = "resolution_mins", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer resolutionMins;

    @NotNull
    @Column(name = "active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SlaPlan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SlaPlan name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFirstResponseMins() {
        return this.firstResponseMins;
    }

    public SlaPlan firstResponseMins(Integer firstResponseMins) {
        this.setFirstResponseMins(firstResponseMins);
        return this;
    }

    public void setFirstResponseMins(Integer firstResponseMins) {
        this.firstResponseMins = firstResponseMins;
    }

    public Integer getResolutionMins() {
        return this.resolutionMins;
    }

    public SlaPlan resolutionMins(Integer resolutionMins) {
        this.setResolutionMins(resolutionMins);
        return this;
    }

    public void setResolutionMins(Integer resolutionMins) {
        this.resolutionMins = resolutionMins;
    }

    public Boolean getActive() {
        return this.active;
    }

    public SlaPlan active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SlaPlan)) {
            return false;
        }
        return getId() != null && getId().equals(((SlaPlan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SlaPlan{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", firstResponseMins=" + getFirstResponseMins() +
            ", resolutionMins=" + getResolutionMins() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
