package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrdersCriteriaTest {

    @Test
    void newOrdersCriteriaHasAllFiltersNullTest() {
        var ordersCriteria = new OrdersCriteria();
        assertThat(ordersCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ordersCriteriaFluentMethodsCreatesFiltersTest() {
        var ordersCriteria = new OrdersCriteria();

        setAllFilters(ordersCriteria);

        assertThat(ordersCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ordersCriteriaCopyCreatesNullFilterTest() {
        var ordersCriteria = new OrdersCriteria();
        var copy = ordersCriteria.copy();

        assertThat(ordersCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ordersCriteria)
        );
    }

    @Test
    void ordersCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ordersCriteria = new OrdersCriteria();
        setAllFilters(ordersCriteria);

        var copy = ordersCriteria.copy();

        assertThat(ordersCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ordersCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ordersCriteria = new OrdersCriteria();

        assertThat(ordersCriteria).hasToString("OrdersCriteria{}");
    }

    private static void setAllFilters(OrdersCriteria ordersCriteria) {
        ordersCriteria.id();
        ordersCriteria.customerId();
        ordersCriteria.code();
        ordersCriteria.status();
        ordersCriteria.paymentStatus();
        ordersCriteria.fulfillmentStatus();
        ordersCriteria.totalAmount();
        ordersCriteria.currency();
        ordersCriteria.note();
        ordersCriteria.placedAt();
        ordersCriteria.updatedAt();
        ordersCriteria.distinct();
    }

    private static Condition<OrdersCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPaymentStatus()) &&
                condition.apply(criteria.getFulfillmentStatus()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getCurrency()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getPlacedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrdersCriteria> copyFiltersAre(OrdersCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPaymentStatus(), copy.getPaymentStatus()) &&
                condition.apply(criteria.getFulfillmentStatus(), copy.getFulfillmentStatus()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getCurrency(), copy.getCurrency()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getPlacedAt(), copy.getPlacedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
