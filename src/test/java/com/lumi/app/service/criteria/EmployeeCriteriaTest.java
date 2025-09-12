package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EmployeeCriteriaTest {

    @Test
    void newEmployeeCriteriaHasAllFiltersNullTest() {
        var employeeCriteria = new EmployeeCriteria();
        assertThat(employeeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void employeeCriteriaFluentMethodsCreatesFiltersTest() {
        var employeeCriteria = new EmployeeCriteria();

        setAllFilters(employeeCriteria);

        assertThat(employeeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void employeeCriteriaCopyCreatesNullFilterTest() {
        var employeeCriteria = new EmployeeCriteria();
        var copy = employeeCriteria.copy();

        assertThat(employeeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(employeeCriteria)
        );
    }

    @Test
    void employeeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var employeeCriteria = new EmployeeCriteria();
        setAllFilters(employeeCriteria);

        var copy = employeeCriteria.copy();

        assertThat(employeeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(employeeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var employeeCriteria = new EmployeeCriteria();

        assertThat(employeeCriteria).hasToString("EmployeeCriteria{}");
    }

    private static void setAllFilters(EmployeeCriteria employeeCriteria) {
        employeeCriteria.id();
        employeeCriteria.code();
        employeeCriteria.fullName();
        employeeCriteria.email();
        employeeCriteria.phone();
        employeeCriteria.role();
        employeeCriteria.status();
        employeeCriteria.department();
        employeeCriteria.joinedAt();
        employeeCriteria.createdAt();
        employeeCriteria.updatedAt();
        employeeCriteria.distinct();
    }

    private static Condition<EmployeeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getDepartment()) &&
                condition.apply(criteria.getJoinedAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EmployeeCriteria> copyFiltersAre(EmployeeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getDepartment(), copy.getDepartment()) &&
                condition.apply(criteria.getJoinedAt(), copy.getJoinedAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
