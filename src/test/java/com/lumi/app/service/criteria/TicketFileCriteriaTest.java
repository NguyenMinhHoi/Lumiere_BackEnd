package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketFileCriteriaTest {

    @Test
    void newTicketFileCriteriaHasAllFiltersNullTest() {
        var ticketFileCriteria = new TicketFileCriteria();
        assertThat(ticketFileCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketFileCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketFileCriteria = new TicketFileCriteria();

        setAllFilters(ticketFileCriteria);

        assertThat(ticketFileCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketFileCriteriaCopyCreatesNullFilterTest() {
        var ticketFileCriteria = new TicketFileCriteria();
        var copy = ticketFileCriteria.copy();

        assertThat(ticketFileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketFileCriteria)
        );
    }

    @Test
    void ticketFileCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketFileCriteria = new TicketFileCriteria();
        setAllFilters(ticketFileCriteria);

        var copy = ticketFileCriteria.copy();

        assertThat(ticketFileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketFileCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketFileCriteria = new TicketFileCriteria();

        assertThat(ticketFileCriteria).hasToString("TicketFileCriteria{}");
    }

    private static void setAllFilters(TicketFileCriteria ticketFileCriteria) {
        ticketFileCriteria.id();
        ticketFileCriteria.fileName();
        ticketFileCriteria.originalName();
        ticketFileCriteria.contentType();
        ticketFileCriteria.capacity();
        ticketFileCriteria.storageType();
        ticketFileCriteria.path();
        ticketFileCriteria.url();
        ticketFileCriteria.checksum();
        ticketFileCriteria.status();
        ticketFileCriteria.uploadedAt();
        ticketFileCriteria.ticketId();
        ticketFileCriteria.uploaderId();
        ticketFileCriteria.distinct();
    }

    private static Condition<TicketFileCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getOriginalName()) &&
                condition.apply(criteria.getContentType()) &&
                condition.apply(criteria.getCapacity()) &&
                condition.apply(criteria.getStorageType()) &&
                condition.apply(criteria.getPath()) &&
                condition.apply(criteria.getUrl()) &&
                condition.apply(criteria.getChecksum()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getUploadedAt()) &&
                condition.apply(criteria.getTicketId()) &&
                condition.apply(criteria.getUploaderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketFileCriteria> copyFiltersAre(TicketFileCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getOriginalName(), copy.getOriginalName()) &&
                condition.apply(criteria.getContentType(), copy.getContentType()) &&
                condition.apply(criteria.getCapacity(), copy.getCapacity()) &&
                condition.apply(criteria.getStorageType(), copy.getStorageType()) &&
                condition.apply(criteria.getPath(), copy.getPath()) &&
                condition.apply(criteria.getUrl(), copy.getUrl()) &&
                condition.apply(criteria.getChecksum(), copy.getChecksum()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getUploadedAt(), copy.getUploadedAt()) &&
                condition.apply(criteria.getTicketId(), copy.getTicketId()) &&
                condition.apply(criteria.getUploaderId(), copy.getUploaderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
