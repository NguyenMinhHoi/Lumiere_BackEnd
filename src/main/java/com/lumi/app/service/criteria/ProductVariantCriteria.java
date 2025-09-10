package com.lumi.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.ProductVariant} entity. This class is used
 * in {@link com.lumi.app.web.rest.ProductVariantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /product-variants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sku;

    private StringFilter name;

    private BigDecimalFilter price;

    private BigDecimalFilter compareAtPrice;

    private StringFilter currency;

    private LongFilter stockQuantity;

    private DoubleFilter weight;

    private DoubleFilter length;

    private DoubleFilter width;

    private DoubleFilter height;

    private BooleanFilter isDefault;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter productId;

    private Boolean distinct;

    public ProductVariantCriteria() {}

    public ProductVariantCriteria(ProductVariantCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sku = other.optionalSku().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.compareAtPrice = other.optionalCompareAtPrice().map(BigDecimalFilter::copy).orElse(null);
        this.currency = other.optionalCurrency().map(StringFilter::copy).orElse(null);
        this.stockQuantity = other.optionalStockQuantity().map(LongFilter::copy).orElse(null);
        this.weight = other.optionalWeight().map(DoubleFilter::copy).orElse(null);
        this.length = other.optionalLength().map(DoubleFilter::copy).orElse(null);
        this.width = other.optionalWidth().map(DoubleFilter::copy).orElse(null);
        this.height = other.optionalHeight().map(DoubleFilter::copy).orElse(null);
        this.isDefault = other.optionalIsDefault().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductVariantCriteria copy() {
        return new ProductVariantCriteria(this);
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

    public StringFilter getSku() {
        return sku;
    }

    public Optional<StringFilter> optionalSku() {
        return Optional.ofNullable(sku);
    }

    public StringFilter sku() {
        if (sku == null) {
            setSku(new StringFilter());
        }
        return sku;
    }

    public void setSku(StringFilter sku) {
        this.sku = sku;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public BigDecimalFilter getCompareAtPrice() {
        return compareAtPrice;
    }

    public Optional<BigDecimalFilter> optionalCompareAtPrice() {
        return Optional.ofNullable(compareAtPrice);
    }

    public BigDecimalFilter compareAtPrice() {
        if (compareAtPrice == null) {
            setCompareAtPrice(new BigDecimalFilter());
        }
        return compareAtPrice;
    }

    public void setCompareAtPrice(BigDecimalFilter compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public StringFilter getCurrency() {
        return currency;
    }

    public Optional<StringFilter> optionalCurrency() {
        return Optional.ofNullable(currency);
    }

    public StringFilter currency() {
        if (currency == null) {
            setCurrency(new StringFilter());
        }
        return currency;
    }

    public void setCurrency(StringFilter currency) {
        this.currency = currency;
    }

    public LongFilter getStockQuantity() {
        return stockQuantity;
    }

    public Optional<LongFilter> optionalStockQuantity() {
        return Optional.ofNullable(stockQuantity);
    }

    public LongFilter stockQuantity() {
        if (stockQuantity == null) {
            setStockQuantity(new LongFilter());
        }
        return stockQuantity;
    }

    public void setStockQuantity(LongFilter stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public DoubleFilter getWeight() {
        return weight;
    }

    public Optional<DoubleFilter> optionalWeight() {
        return Optional.ofNullable(weight);
    }

    public DoubleFilter weight() {
        if (weight == null) {
            setWeight(new DoubleFilter());
        }
        return weight;
    }

    public void setWeight(DoubleFilter weight) {
        this.weight = weight;
    }

    public DoubleFilter getLength() {
        return length;
    }

    public Optional<DoubleFilter> optionalLength() {
        return Optional.ofNullable(length);
    }

    public DoubleFilter length() {
        if (length == null) {
            setLength(new DoubleFilter());
        }
        return length;
    }

    public void setLength(DoubleFilter length) {
        this.length = length;
    }

    public DoubleFilter getWidth() {
        return width;
    }

    public Optional<DoubleFilter> optionalWidth() {
        return Optional.ofNullable(width);
    }

    public DoubleFilter width() {
        if (width == null) {
            setWidth(new DoubleFilter());
        }
        return width;
    }

    public void setWidth(DoubleFilter width) {
        this.width = width;
    }

    public DoubleFilter getHeight() {
        return height;
    }

    public Optional<DoubleFilter> optionalHeight() {
        return Optional.ofNullable(height);
    }

    public DoubleFilter height() {
        if (height == null) {
            setHeight(new DoubleFilter());
        }
        return height;
    }

    public void setHeight(DoubleFilter height) {
        this.height = height;
    }

    public BooleanFilter getIsDefault() {
        return isDefault;
    }

    public Optional<BooleanFilter> optionalIsDefault() {
        return Optional.ofNullable(isDefault);
    }

    public BooleanFilter isDefault() {
        if (isDefault == null) {
            setIsDefault(new BooleanFilter());
        }
        return isDefault;
    }

    public void setIsDefault(BooleanFilter isDefault) {
        this.isDefault = isDefault;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
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

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
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
        final ProductVariantCriteria that = (ProductVariantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sku, that.sku) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(compareAtPrice, that.compareAtPrice) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(stockQuantity, that.stockQuantity) &&
            Objects.equals(weight, that.weight) &&
            Objects.equals(length, that.length) &&
            Objects.equals(width, that.width) &&
            Objects.equals(height, that.height) &&
            Objects.equals(isDefault, that.isDefault) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sku,
            name,
            price,
            compareAtPrice,
            currency,
            stockQuantity,
            weight,
            length,
            width,
            height,
            isDefault,
            createdAt,
            updatedAt,
            productId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariantCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSku().map(f -> "sku=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalCompareAtPrice().map(f -> "compareAtPrice=" + f + ", ").orElse("") +
            optionalCurrency().map(f -> "currency=" + f + ", ").orElse("") +
            optionalStockQuantity().map(f -> "stockQuantity=" + f + ", ").orElse("") +
            optionalWeight().map(f -> "weight=" + f + ", ").orElse("") +
            optionalLength().map(f -> "length=" + f + ", ").orElse("") +
            optionalWidth().map(f -> "width=" + f + ", ").orElse("") +
            optionalHeight().map(f -> "height=" + f + ", ").orElse("") +
            optionalIsDefault().map(f -> "isDefault=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
