package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class IntegrationLogCriteriaTest {

    @Test
    void newIntegrationLogCriteriaHasAllFiltersNullTest() {
        var integrationLogCriteria = new IntegrationLogCriteria();
        assertThat(integrationLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void integrationLogCriteriaFluentMethodsCreatesFiltersTest() {
        var integrationLogCriteria = new IntegrationLogCriteria();

        setAllFilters(integrationLogCriteria);

        assertThat(integrationLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void integrationLogCriteriaCopyCreatesNullFilterTest() {
        var integrationLogCriteria = new IntegrationLogCriteria();
        var copy = integrationLogCriteria.copy();

        assertThat(integrationLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(integrationLogCriteria)
        );
    }

    @Test
    void integrationLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var integrationLogCriteria = new IntegrationLogCriteria();
        setAllFilters(integrationLogCriteria);

        var copy = integrationLogCriteria.copy();

        assertThat(integrationLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(integrationLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var integrationLogCriteria = new IntegrationLogCriteria();

        assertThat(integrationLogCriteria).hasToString("IntegrationLogCriteria{}");
    }

    private static void setAllFilters(IntegrationLogCriteria integrationLogCriteria) {
        integrationLogCriteria.id();
        integrationLogCriteria.sourceApp();
        integrationLogCriteria.targetApp();
        integrationLogCriteria.status();
        integrationLogCriteria.retries();
        integrationLogCriteria.createdAt();
        integrationLogCriteria.updatedAt();
        integrationLogCriteria.distinct();
    }

    private static Condition<IntegrationLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSourceApp()) &&
                condition.apply(criteria.getTargetApp()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRetries()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<IntegrationLogCriteria> copyFiltersAre(
        IntegrationLogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSourceApp(), copy.getSourceApp()) &&
                condition.apply(criteria.getTargetApp(), copy.getTargetApp()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRetries(), copy.getRetries()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
