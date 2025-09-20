package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClothProductMapCriteriaTest {

    @Test
    void newClothProductMapCriteriaHasAllFiltersNullTest() {
        var clothProductMapCriteria = new ClothProductMapCriteria();
        assertThat(clothProductMapCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void clothProductMapCriteriaFluentMethodsCreatesFiltersTest() {
        var clothProductMapCriteria = new ClothProductMapCriteria();

        setAllFilters(clothProductMapCriteria);

        assertThat(clothProductMapCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void clothProductMapCriteriaCopyCreatesNullFilterTest() {
        var clothProductMapCriteria = new ClothProductMapCriteria();
        var copy = clothProductMapCriteria.copy();

        assertThat(clothProductMapCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(clothProductMapCriteria)
        );
    }

    @Test
    void clothProductMapCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var clothProductMapCriteria = new ClothProductMapCriteria();
        setAllFilters(clothProductMapCriteria);

        var copy = clothProductMapCriteria.copy();

        assertThat(clothProductMapCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(clothProductMapCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var clothProductMapCriteria = new ClothProductMapCriteria();

        assertThat(clothProductMapCriteria).hasToString("ClothProductMapCriteria{}");
    }

    private static void setAllFilters(ClothProductMapCriteria clothProductMapCriteria) {
        clothProductMapCriteria.id();
        clothProductMapCriteria.clothId();
        clothProductMapCriteria.productId();
        clothProductMapCriteria.quantity();
        clothProductMapCriteria.unit();
        clothProductMapCriteria.note();
        clothProductMapCriteria.createdAt();
        clothProductMapCriteria.distinct();
    }

    private static Condition<ClothProductMapCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getClothId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnit()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClothProductMapCriteria> copyFiltersAre(
        ClothProductMapCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getClothId(), copy.getClothId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnit(), copy.getUnit()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
