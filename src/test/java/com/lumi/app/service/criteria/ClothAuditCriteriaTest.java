package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClothAuditCriteriaTest {

    @Test
    void newClothAuditCriteriaHasAllFiltersNullTest() {
        var clothAuditCriteria = new ClothAuditCriteria();
        assertThat(clothAuditCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void clothAuditCriteriaFluentMethodsCreatesFiltersTest() {
        var clothAuditCriteria = new ClothAuditCriteria();

        setAllFilters(clothAuditCriteria);

        assertThat(clothAuditCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void clothAuditCriteriaCopyCreatesNullFilterTest() {
        var clothAuditCriteria = new ClothAuditCriteria();
        var copy = clothAuditCriteria.copy();

        assertThat(clothAuditCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(clothAuditCriteria)
        );
    }

    @Test
    void clothAuditCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var clothAuditCriteria = new ClothAuditCriteria();
        setAllFilters(clothAuditCriteria);

        var copy = clothAuditCriteria.copy();

        assertThat(clothAuditCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(clothAuditCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var clothAuditCriteria = new ClothAuditCriteria();

        assertThat(clothAuditCriteria).hasToString("ClothAuditCriteria{}");
    }

    private static void setAllFilters(ClothAuditCriteria clothAuditCriteria) {
        clothAuditCriteria.id();
        clothAuditCriteria.clothId();
        clothAuditCriteria.supplierId();
        clothAuditCriteria.productId();
        clothAuditCriteria.action();
        clothAuditCriteria.quantity();
        clothAuditCriteria.unit();
        clothAuditCriteria.sentAt();
        clothAuditCriteria.note();
        clothAuditCriteria.createdAt();
        clothAuditCriteria.distinct();
    }

    private static Condition<ClothAuditCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getClothId()) &&
                condition.apply(criteria.getSupplierId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnit()) &&
                condition.apply(criteria.getSentAt()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClothAuditCriteria> copyFiltersAre(ClothAuditCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getClothId(), copy.getClothId()) &&
                condition.apply(criteria.getSupplierId(), copy.getSupplierId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnit(), copy.getUnit()) &&
                condition.apply(criteria.getSentAt(), copy.getSentAt()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
