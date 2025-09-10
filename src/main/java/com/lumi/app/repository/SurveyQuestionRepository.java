package com.lumi.app.repository;

import com.lumi.app.domain.SurveyQuestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SurveyQuestion entity.
 */
@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    default Optional<SurveyQuestion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SurveyQuestion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SurveyQuestion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select surveyQuestion from SurveyQuestion surveyQuestion left join fetch surveyQuestion.survey",
        countQuery = "select count(surveyQuestion) from SurveyQuestion surveyQuestion"
    )
    Page<SurveyQuestion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select surveyQuestion from SurveyQuestion surveyQuestion left join fetch surveyQuestion.survey")
    List<SurveyQuestion> findAllWithToOneRelationships();

    @Query("select surveyQuestion from SurveyQuestion surveyQuestion left join fetch surveyQuestion.survey where surveyQuestion.id =:id")
    Optional<SurveyQuestion> findOneWithToOneRelationships(@Param("id") Long id);
}
