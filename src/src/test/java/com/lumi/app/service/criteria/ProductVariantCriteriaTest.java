package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProductVariantCriteriaTest {

    @Test
    void newProductVariantCriteriaHasAllFiltersNullTest() {
        var productVariantCriteria = new ProductVariantCriteria();
        assertThat(productVariantCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void productVariantCriteriaFluentMethodsCreatesFiltersTest() {
        var productVariantCriteria = new ProductVariantCriteria();

        setAllFilters(productVariantCriteria);

        assertThat(productVariantCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void productVariantCriteriaCopyCreatesNullFilterTest() {
        var productVariantCriteria = new ProductVariantCriteria();
        var copy = productVariantCriteria.copy();

        assertThat(productVariantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(productVariantCriteria)
        );
    }

    @Test
    void productVariantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var productVariantCriteria = new ProductVariantCriteria();
        setAllFilters(productVariantCriteria);

        var copy = productVariantCriteria.copy();

        assertThat(productVariantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(productVariantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var productVariantCriteria = new ProductVariantCriteria();

        assertThat(productVariantCriteria).hasToString("ProductVariantCriteria{}");
    }

    private static void setAllFilters(ProductVariantCriteria productVariantCriteria) {
        productVariantCriteria.id();
        productVariantCriteria.productId();
        productVariantCriteria.sku();
        productVariantCriteria.name();
        productVariantCriteria.price();
        productVariantCriteria.compareAtPrice();
        productVariantCriteria.currency();
        productVariantCriteria.stockQuantity();
        productVariantCriteria.weight();
        productVariantCriteria.length();
        productVariantCriteria.width();
        productVariantCriteria.height();
        productVariantCriteria.isDefault();
        productVariantCriteria.createdAt();
        productVariantCriteria.updatedAt();
        productVariantCriteria.distinct();
    }

    private static Condition<ProductVariantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getSku()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getCompareAtPrice()) &&
                condition.apply(criteria.getCurrency()) &&
                condition.apply(criteria.getStockQuantity()) &&
                condition.apply(criteria.getWeight()) &&
                condition.apply(criteria.getLength()) &&
                condition.apply(criteria.getWidth()) &&
                condition.apply(criteria.getHeight()) &&
                condition.apply(criteria.getIsDefault()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProductVariantCriteria> copyFiltersAre(
        ProductVariantCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getSku(), copy.getSku()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getCompareAtPrice(), copy.getCompareAtPrice()) &&
                condition.apply(criteria.getCurrency(), copy.getCurrency()) &&
                condition.apply(criteria.getStockQuantity(), copy.getStockQuantity()) &&
                condition.apply(criteria.getWeight(), copy.getWeight()) &&
                condition.apply(criteria.getLength(), copy.getLength()) &&
                condition.apply(criteria.getWidth(), copy.getWidth()) &&
                condition.apply(criteria.getHeight(), copy.getHeight()) &&
                condition.apply(criteria.getIsDefault(), copy.getIsDefault()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
