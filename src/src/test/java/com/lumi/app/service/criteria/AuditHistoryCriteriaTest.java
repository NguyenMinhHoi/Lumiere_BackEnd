package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AuditHistoryCriteriaTest {

    @Test
    void newAuditHistoryCriteriaHasAllFiltersNullTest() {
        var auditHistoryCriteria = new AuditHistoryCriteria();
        assertThat(auditHistoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void auditHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var auditHistoryCriteria = new AuditHistoryCriteria();

        setAllFilters(auditHistoryCriteria);

        assertThat(auditHistoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void auditHistoryCriteriaCopyCreatesNullFilterTest() {
        var auditHistoryCriteria = new AuditHistoryCriteria();
        var copy = auditHistoryCriteria.copy();

        assertThat(auditHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(auditHistoryCriteria)
        );
    }

    @Test
    void auditHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var auditHistoryCriteria = new AuditHistoryCriteria();
        setAllFilters(auditHistoryCriteria);

        var copy = auditHistoryCriteria.copy();

        assertThat(auditHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(auditHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var auditHistoryCriteria = new AuditHistoryCriteria();

        assertThat(auditHistoryCriteria).hasToString("AuditHistoryCriteria{}");
    }

    private static void setAllFilters(AuditHistoryCriteria auditHistoryCriteria) {
        auditHistoryCriteria.id();
        auditHistoryCriteria.entityName();
        auditHistoryCriteria.entityId();
        auditHistoryCriteria.action();
        auditHistoryCriteria.performedBy();
        auditHistoryCriteria.performedAt();
        auditHistoryCriteria.ipAddress();
        auditHistoryCriteria.distinct();
    }

    private static Condition<AuditHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEntityName()) &&
                condition.apply(criteria.getEntityId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getPerformedBy()) &&
                condition.apply(criteria.getPerformedAt()) &&
                condition.apply(criteria.getIpAddress()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AuditHistoryCriteria> copyFiltersAre(
        AuditHistoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEntityName(), copy.getEntityName()) &&
                condition.apply(criteria.getEntityId(), copy.getEntityId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getPerformedBy(), copy.getPerformedBy()) &&
                condition.apply(criteria.getPerformedAt(), copy.getPerformedAt()) &&
                condition.apply(criteria.getIpAddress(), copy.getIpAddress()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
