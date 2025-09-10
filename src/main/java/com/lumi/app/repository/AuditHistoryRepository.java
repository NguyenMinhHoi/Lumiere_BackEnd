package com.lumi.app.repository;

import com.lumi.app.domain.AuditHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuditHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditHistoryRepository extends JpaRepository<AuditHistory, Long>, JpaSpecificationExecutor<AuditHistory> {}
