package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClothCriteriaTest {

    @Test
    void newClothCriteriaHasAllFiltersNullTest() {
        var clothCriteria = new ClothCriteria();
        assertThat(clothCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void clothCriteriaFluentMethodsCreatesFiltersTest() {
        var clothCriteria = new ClothCriteria();

        setAllFilters(clothCriteria);

        assertThat(clothCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void clothCriteriaCopyCreatesNullFilterTest() {
        var clothCriteria = new ClothCriteria();
        var copy = clothCriteria.copy();

        assertThat(clothCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(clothCriteria)
        );
    }

    @Test
    void clothCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var clothCriteria = new ClothCriteria();
        setAllFilters(clothCriteria);

        var copy = clothCriteria.copy();

        assertThat(clothCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(clothCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var clothCriteria = new ClothCriteria();

        assertThat(clothCriteria).hasToString("ClothCriteria{}");
    }

    private static void setAllFilters(ClothCriteria clothCriteria) {
        clothCriteria.id();
        clothCriteria.code();
        clothCriteria.name();
        clothCriteria.material();
        clothCriteria.color();
        clothCriteria.width();
        clothCriteria.length();
        clothCriteria.unit();
        clothCriteria.status();
        clothCriteria.createdAt();
        clothCriteria.updatedAt();
        clothCriteria.distinct();
    }

    private static Condition<ClothCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getMaterial()) &&
                condition.apply(criteria.getColor()) &&
                condition.apply(criteria.getWidth()) &&
                condition.apply(criteria.getLength()) &&
                condition.apply(criteria.getUnit()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClothCriteria> copyFiltersAre(ClothCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getMaterial(), copy.getMaterial()) &&
                condition.apply(criteria.getColor(), copy.getColor()) &&
                condition.apply(criteria.getWidth(), copy.getWidth()) &&
                condition.apply(criteria.getLength(), copy.getLength()) &&
                condition.apply(criteria.getUnit(), copy.getUnit()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
