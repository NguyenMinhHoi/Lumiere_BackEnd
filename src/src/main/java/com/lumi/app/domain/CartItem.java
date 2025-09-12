package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CartItem.
 */
@Entity
@Table(name = "cart_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cartitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @NotNull
    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer quantity;

    @NotNull
    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CartItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return this.cartId;
    }

    public CartItem cartId(Long cartId) {
        this.setCartId(cartId);
        return this;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductVariantId() {
        return this.productVariantId;
    }

    public CartItem productVariantId(Long productVariantId) {
        this.setProductVariantId(productVariantId);
        return this;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public CartItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Instant getAddedAt() {
        return this.addedAt;
    }

    public CartItem addedAt(Instant addedAt) {
        this.setAddedAt(addedAt);
        return this;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartItem)) {
            return false;
        }
        return getId() != null && getId().equals(((CartItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItem{" +
            "id=" + getId() +
            ", cartId=" + getCartId() +
            ", productVariantId=" + getProductVariantId() +
            ", quantity=" + getQuantity() +
            ", addedAt='" + getAddedAt() + "'" +
            "}";
    }
}
