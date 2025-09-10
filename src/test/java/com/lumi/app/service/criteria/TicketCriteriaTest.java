package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketCriteriaTest {

    @Test
    void newTicketCriteriaHasAllFiltersNullTest() {
        var ticketCriteria = new TicketCriteria();
        assertThat(ticketCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketCriteria = new TicketCriteria();

        setAllFilters(ticketCriteria);

        assertThat(ticketCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketCriteriaCopyCreatesNullFilterTest() {
        var ticketCriteria = new TicketCriteria();
        var copy = ticketCriteria.copy();

        assertThat(ticketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCriteria)
        );
    }

    @Test
    void ticketCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketCriteria = new TicketCriteria();
        setAllFilters(ticketCriteria);

        var copy = ticketCriteria.copy();

        assertThat(ticketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketCriteria = new TicketCriteria();

        assertThat(ticketCriteria).hasToString("TicketCriteria{}");
    }

    private static void setAllFilters(TicketCriteria ticketCriteria) {
        ticketCriteria.id();
        ticketCriteria.code();
        ticketCriteria.subject();
        ticketCriteria.status();
        ticketCriteria.priority();
        ticketCriteria.channel();
        ticketCriteria.openedAt();
        ticketCriteria.firstResponseAt();
        ticketCriteria.resolvedAt();
        ticketCriteria.slaDueAt();
        ticketCriteria.customerId();
        ticketCriteria.assigneeId();
        ticketCriteria.slaPlanId();
        ticketCriteria.orderId();
        ticketCriteria.tagsId();
        ticketCriteria.distinct();
    }

    private static Condition<TicketCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getSubject()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPriority()) &&
                condition.apply(criteria.getChannel()) &&
                condition.apply(criteria.getOpenedAt()) &&
                condition.apply(criteria.getFirstResponseAt()) &&
                condition.apply(criteria.getResolvedAt()) &&
                condition.apply(criteria.getSlaDueAt()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getAssigneeId()) &&
                condition.apply(criteria.getSlaPlanId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getTagsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketCriteria> copyFiltersAre(TicketCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getSubject(), copy.getSubject()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPriority(), copy.getPriority()) &&
                condition.apply(criteria.getChannel(), copy.getChannel()) &&
                condition.apply(criteria.getOpenedAt(), copy.getOpenedAt()) &&
                condition.apply(criteria.getFirstResponseAt(), copy.getFirstResponseAt()) &&
                condition.apply(criteria.getResolvedAt(), copy.getResolvedAt()) &&
                condition.apply(criteria.getSlaDueAt(), copy.getSlaDueAt()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getAssigneeId(), copy.getAssigneeId()) &&
                condition.apply(criteria.getSlaPlanId(), copy.getSlaPlanId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getTagsId(), copy.getTagsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
