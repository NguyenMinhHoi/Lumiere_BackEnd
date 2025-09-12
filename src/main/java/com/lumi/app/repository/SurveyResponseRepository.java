package com.lumi.app.repository;

import com.lumi.app.domain.SurveyResponse;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SurveyResponse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {}
