package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ClothProductMap.
 */
@Entity
@Table(name = "cloth_product_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "clothproductmap")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothProductMap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "cloth_id", nullable = false)
    private Long clothId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Size(max = 16)
    @Column(name = "unit", length = 16)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String unit;

    @Size(max = 255)
    @Column(name = "note", length = 255)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String note;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClothProductMap id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClothId() {
        return this.clothId;
    }

    public ClothProductMap clothId(Long clothId) {
        this.setClothId(clothId);
        return this;
    }

    public void setClothId(Long clothId) {
        this.clothId = clothId;
    }

    public Long getProductId() {
        return this.productId;
    }

    public ClothProductMap productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public ClothProductMap quantity(Double quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return this.unit;
    }

    public ClothProductMap unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNote() {
        return this.note;
    }

    public ClothProductMap note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ClothProductMap createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClothProductMap)) {
            return false;
        }
        return getId() != null && getId().equals(((ClothProductMap) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothProductMap{" +
            "id=" + getId() +
            ", clothId=" + getClothId() +
            ", productId=" + getProductId() +
            ", quantity=" + getQuantity() +
            ", unit='" + getUnit() + "'" +
            ", note='" + getNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
