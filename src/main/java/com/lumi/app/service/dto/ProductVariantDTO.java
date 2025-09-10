package com.lumi.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.ProductVariant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariantDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    private String sku;

    @NotNull
    @Size(min = 2, max = 200)
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal price;

    private BigDecimal compareAtPrice;

    @Size(max = 3)
    private String currency;

    @NotNull
    @Min(value = 0L)
    private Long stockQuantity;

    private Double weight;

    private Double length;

    private Double width;

    private Double height;

    @NotNull
    private Boolean isDefault;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariantDTO)) {
            return false;
        }

        ProductVariantDTO productVariantDTO = (ProductVariantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productVariantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariantDTO{" +
            "id=" + getId() +
            ", sku='" + getSku() + "'" +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", compareAtPrice=" + getCompareAtPrice() +
            ", currency='" + getCurrency() + "'" +
            ", stockQuantity=" + getStockQuantity() +
            ", weight=" + getWeight() +
            ", length=" + getLength() +
            ", width=" + getWidth() +
            ", height=" + getHeight() +
            ", isDefault='" + getIsDefault() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
