package com.lumi.app.repository;

import com.lumi.app.domain.IntegrationLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the IntegrationLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long>, JpaSpecificationExecutor<IntegrationLog> {}
