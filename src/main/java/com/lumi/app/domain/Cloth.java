package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.ClothStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Cloth.
 */
@Entity
@Table(name = "cloth")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cloth")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cloth implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    @Column(name = "code", length = 64, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @NotNull
    @Size(min = 2, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Size(max = 128)
    @Column(name = "material", length = 128)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String material;

    @Size(max = 64)
    @Column(name = "color", length = 64)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String color;

    @Column(name = "width")
    private Double width;

    @Column(name = "length")
    private Double length;

    @Size(max = 16)
    @Column(name = "unit", length = 16)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String unit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ClothStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cloth id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Cloth code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Cloth name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial() {
        return this.material;
    }

    public Cloth material(String material) {
        this.setMaterial(material);
        return this;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColor() {
        return this.color;
    }

    public Cloth color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getWidth() {
        return this.width;
    }

    public Cloth width(Double width) {
        this.setWidth(width);
        return this;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getLength() {
        return this.length;
    }

    public Cloth length(Double length) {
        this.setLength(length);
        return this;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getUnit() {
        return this.unit;
    }

    public Cloth unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ClothStatus getStatus() {
        return this.status;
    }

    public Cloth status(ClothStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ClothStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Cloth createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Cloth updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cloth)) {
            return false;
        }
        return getId() != null && getId().equals(((Cloth) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cloth{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", material='" + getMaterial() + "'" +
            ", color='" + getColor() + "'" +
            ", width=" + getWidth() +
            ", length=" + getLength() +
            ", unit='" + getUnit() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
