package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VoucherCriteriaTest {

    @Test
    void newVoucherCriteriaHasAllFiltersNullTest() {
        var voucherCriteria = new VoucherCriteria();
        assertThat(voucherCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void voucherCriteriaFluentMethodsCreatesFiltersTest() {
        var voucherCriteria = new VoucherCriteria();

        setAllFilters(voucherCriteria);

        assertThat(voucherCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void voucherCriteriaCopyCreatesNullFilterTest() {
        var voucherCriteria = new VoucherCriteria();
        var copy = voucherCriteria.copy();

        assertThat(voucherCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(voucherCriteria)
        );
    }

    @Test
    void voucherCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var voucherCriteria = new VoucherCriteria();
        setAllFilters(voucherCriteria);

        var copy = voucherCriteria.copy();

        assertThat(voucherCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(voucherCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var voucherCriteria = new VoucherCriteria();

        assertThat(voucherCriteria).hasToString("VoucherCriteria{}");
    }

    private static void setAllFilters(VoucherCriteria voucherCriteria) {
        voucherCriteria.id();
        voucherCriteria.code();
        voucherCriteria.discountType();
        voucherCriteria.discountValue();
        voucherCriteria.minOrderValue();
        voucherCriteria.maxDiscountValue();
        voucherCriteria.usageLimit();
        voucherCriteria.usedCount();
        voucherCriteria.validFrom();
        voucherCriteria.validTo();
        voucherCriteria.status();
        voucherCriteria.createdAt();
        voucherCriteria.updatedAt();
        voucherCriteria.distinct();
    }

    private static Condition<VoucherCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getDiscountType()) &&
                condition.apply(criteria.getDiscountValue()) &&
                condition.apply(criteria.getMinOrderValue()) &&
                condition.apply(criteria.getMaxDiscountValue()) &&
                condition.apply(criteria.getUsageLimit()) &&
                condition.apply(criteria.getUsedCount()) &&
                condition.apply(criteria.getValidFrom()) &&
                condition.apply(criteria.getValidTo()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VoucherCriteria> copyFiltersAre(VoucherCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getDiscountType(), copy.getDiscountType()) &&
                condition.apply(criteria.getDiscountValue(), copy.getDiscountValue()) &&
                condition.apply(criteria.getMinOrderValue(), copy.getMinOrderValue()) &&
                condition.apply(criteria.getMaxDiscountValue(), copy.getMaxDiscountValue()) &&
                condition.apply(criteria.getUsageLimit(), copy.getUsageLimit()) &&
                condition.apply(criteria.getUsedCount(), copy.getUsedCount()) &&
                condition.apply(criteria.getValidFrom(), copy.getValidFrom()) &&
                condition.apply(criteria.getValidTo(), copy.getValidTo()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
