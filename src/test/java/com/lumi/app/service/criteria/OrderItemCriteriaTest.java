package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrderItemCriteriaTest {

    @Test
    void newOrderItemCriteriaHasAllFiltersNullTest() {
        var orderItemCriteria = new OrderItemCriteria();
        assertThat(orderItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void orderItemCriteriaFluentMethodsCreatesFiltersTest() {
        var orderItemCriteria = new OrderItemCriteria();

        setAllFilters(orderItemCriteria);

        assertThat(orderItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void orderItemCriteriaCopyCreatesNullFilterTest() {
        var orderItemCriteria = new OrderItemCriteria();
        var copy = orderItemCriteria.copy();

        assertThat(orderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(orderItemCriteria)
        );
    }

    @Test
    void orderItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var orderItemCriteria = new OrderItemCriteria();
        setAllFilters(orderItemCriteria);

        var copy = orderItemCriteria.copy();

        assertThat(orderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(orderItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var orderItemCriteria = new OrderItemCriteria();

        assertThat(orderItemCriteria).hasToString("OrderItemCriteria{}");
    }

    private static void setAllFilters(OrderItemCriteria orderItemCriteria) {
        orderItemCriteria.id();
        orderItemCriteria.quantity();
        orderItemCriteria.unitPrice();
        orderItemCriteria.totalPrice();
        orderItemCriteria.nameSnapshot();
        orderItemCriteria.skuSnapshot();
        orderItemCriteria.orderId();
        orderItemCriteria.variantId();
        orderItemCriteria.distinct();
    }

    private static Condition<OrderItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getTotalPrice()) &&
                condition.apply(criteria.getNameSnapshot()) &&
                condition.apply(criteria.getSkuSnapshot()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getVariantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrderItemCriteria> copyFiltersAre(OrderItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getTotalPrice(), copy.getTotalPrice()) &&
                condition.apply(criteria.getNameSnapshot(), copy.getNameSnapshot()) &&
                condition.apply(criteria.getSkuSnapshot(), copy.getSkuSnapshot()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getVariantId(), copy.getVariantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
