package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SupplementCriteriaTest {

    @Test
    void newSupplementCriteriaHasAllFiltersNullTest() {
        var supplementCriteria = new SupplementCriteria();
        assertThat(supplementCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void supplementCriteriaFluentMethodsCreatesFiltersTest() {
        var supplementCriteria = new SupplementCriteria();

        setAllFilters(supplementCriteria);

        assertThat(supplementCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void supplementCriteriaCopyCreatesNullFilterTest() {
        var supplementCriteria = new SupplementCriteria();
        var copy = supplementCriteria.copy();

        assertThat(supplementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(supplementCriteria)
        );
    }

    @Test
    void supplementCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var supplementCriteria = new SupplementCriteria();
        setAllFilters(supplementCriteria);

        var copy = supplementCriteria.copy();

        assertThat(supplementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(supplementCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var supplementCriteria = new SupplementCriteria();

        assertThat(supplementCriteria).hasToString("SupplementCriteria{}");
    }

    private static void setAllFilters(SupplementCriteria supplementCriteria) {
        supplementCriteria.id();
        supplementCriteria.supplyPrice();
        supplementCriteria.currency();
        supplementCriteria.leadTimeDays();
        supplementCriteria.minOrderQty();
        supplementCriteria.isPreferred();
        supplementCriteria.createdAt();
        supplementCriteria.updatedAt();
        supplementCriteria.productId();
        supplementCriteria.supplierId();
        supplementCriteria.distinct();
    }

    private static Condition<SupplementCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSupplyPrice()) &&
                condition.apply(criteria.getCurrency()) &&
                condition.apply(criteria.getLeadTimeDays()) &&
                condition.apply(criteria.getMinOrderQty()) &&
                condition.apply(criteria.getIsPreferred()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getSupplierId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SupplementCriteria> copyFiltersAre(SupplementCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSupplyPrice(), copy.getSupplyPrice()) &&
                condition.apply(criteria.getCurrency(), copy.getCurrency()) &&
                condition.apply(criteria.getLeadTimeDays(), copy.getLeadTimeDays()) &&
                condition.apply(criteria.getMinOrderQty(), copy.getMinOrderQty()) &&
                condition.apply(criteria.getIsPreferred(), copy.getIsPreferred()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getSupplierId(), copy.getSupplierId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
