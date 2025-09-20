package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClothSupplementCriteriaTest {

    @Test
    void newClothSupplementCriteriaHasAllFiltersNullTest() {
        var clothSupplementCriteria = new ClothSupplementCriteria();
        assertThat(clothSupplementCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void clothSupplementCriteriaFluentMethodsCreatesFiltersTest() {
        var clothSupplementCriteria = new ClothSupplementCriteria();

        setAllFilters(clothSupplementCriteria);

        assertThat(clothSupplementCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void clothSupplementCriteriaCopyCreatesNullFilterTest() {
        var clothSupplementCriteria = new ClothSupplementCriteria();
        var copy = clothSupplementCriteria.copy();

        assertThat(clothSupplementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(clothSupplementCriteria)
        );
    }

    @Test
    void clothSupplementCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var clothSupplementCriteria = new ClothSupplementCriteria();
        setAllFilters(clothSupplementCriteria);

        var copy = clothSupplementCriteria.copy();

        assertThat(clothSupplementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(clothSupplementCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var clothSupplementCriteria = new ClothSupplementCriteria();

        assertThat(clothSupplementCriteria).hasToString("ClothSupplementCriteria{}");
    }

    private static void setAllFilters(ClothSupplementCriteria clothSupplementCriteria) {
        clothSupplementCriteria.id();
        clothSupplementCriteria.clothId();
        clothSupplementCriteria.supplierId();
        clothSupplementCriteria.supplyPrice();
        clothSupplementCriteria.currency();
        clothSupplementCriteria.leadTimeDays();
        clothSupplementCriteria.minOrderQty();
        clothSupplementCriteria.isPreferred();
        clothSupplementCriteria.createdAt();
        clothSupplementCriteria.updatedAt();
        clothSupplementCriteria.distinct();
    }

    private static Condition<ClothSupplementCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getClothId()) &&
                condition.apply(criteria.getSupplierId()) &&
                condition.apply(criteria.getSupplyPrice()) &&
                condition.apply(criteria.getCurrency()) &&
                condition.apply(criteria.getLeadTimeDays()) &&
                condition.apply(criteria.getMinOrderQty()) &&
                condition.apply(criteria.getIsPreferred()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClothSupplementCriteria> copyFiltersAre(
        ClothSupplementCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getClothId(), copy.getClothId()) &&
                condition.apply(criteria.getSupplierId(), copy.getSupplierId()) &&
                condition.apply(criteria.getSupplyPrice(), copy.getSupplyPrice()) &&
                condition.apply(criteria.getCurrency(), copy.getCurrency()) &&
                condition.apply(criteria.getLeadTimeDays(), copy.getLeadTimeDays()) &&
                condition.apply(criteria.getMinOrderQty(), copy.getMinOrderQty()) &&
                condition.apply(criteria.getIsPreferred(), copy.getIsPreferred()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
