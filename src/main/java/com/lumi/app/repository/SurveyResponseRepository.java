package com.lumi.app.repository;

import com.lumi.app.domain.SurveyResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SurveyResponse entity.
 */
@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    default Optional<SurveyResponse> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SurveyResponse> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SurveyResponse> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select surveyResponse from SurveyResponse surveyResponse left join fetch surveyResponse.survey left join fetch surveyResponse.customer left join fetch surveyResponse.ticket",
        countQuery = "select count(surveyResponse) from SurveyResponse surveyResponse"
    )
    Page<SurveyResponse> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select surveyResponse from SurveyResponse surveyResponse left join fetch surveyResponse.survey left join fetch surveyResponse.customer left join fetch surveyResponse.ticket"
    )
    List<SurveyResponse> findAllWithToOneRelationships();

    @Query(
        "select surveyResponse from SurveyResponse surveyResponse left join fetch surveyResponse.survey left join fetch surveyResponse.customer left join fetch surveyResponse.ticket where surveyResponse.id =:id"
    )
    Optional<SurveyResponse> findOneWithToOneRelationships(@Param("id") Long id);
}
