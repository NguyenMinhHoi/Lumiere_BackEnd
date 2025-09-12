package com.lumi.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class KnowledgeArticleCriteriaTest {

    @Test
    void newKnowledgeArticleCriteriaHasAllFiltersNullTest() {
        var knowledgeArticleCriteria = new KnowledgeArticleCriteria();
        assertThat(knowledgeArticleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void knowledgeArticleCriteriaFluentMethodsCreatesFiltersTest() {
        var knowledgeArticleCriteria = new KnowledgeArticleCriteria();

        setAllFilters(knowledgeArticleCriteria);

        assertThat(knowledgeArticleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void knowledgeArticleCriteriaCopyCreatesNullFilterTest() {
        var knowledgeArticleCriteria = new KnowledgeArticleCriteria();
        var copy = knowledgeArticleCriteria.copy();

        assertThat(knowledgeArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(knowledgeArticleCriteria)
        );
    }

    @Test
    void knowledgeArticleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var knowledgeArticleCriteria = new KnowledgeArticleCriteria();
        setAllFilters(knowledgeArticleCriteria);

        var copy = knowledgeArticleCriteria.copy();

        assertThat(knowledgeArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(knowledgeArticleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var knowledgeArticleCriteria = new KnowledgeArticleCriteria();

        assertThat(knowledgeArticleCriteria).hasToString("KnowledgeArticleCriteria{}");
    }

    private static void setAllFilters(KnowledgeArticleCriteria knowledgeArticleCriteria) {
        knowledgeArticleCriteria.id();
        knowledgeArticleCriteria.categoryId();
        knowledgeArticleCriteria.title();
        knowledgeArticleCriteria.published();
        knowledgeArticleCriteria.views();
        knowledgeArticleCriteria.updatedAt();
        knowledgeArticleCriteria.distinct();
    }

    private static Condition<KnowledgeArticleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCategoryId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getPublished()) &&
                condition.apply(criteria.getViews()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<KnowledgeArticleCriteria> copyFiltersAre(
        KnowledgeArticleCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCategoryId(), copy.getCategoryId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getPublished(), copy.getPublished()) &&
                condition.apply(criteria.getViews(), copy.getViews()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
