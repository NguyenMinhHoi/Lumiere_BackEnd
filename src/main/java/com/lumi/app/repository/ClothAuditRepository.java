package com.lumi.app.repository;

import com.lumi.app.domain.ClothAudit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClothAudit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClothAuditRepository extends JpaRepository<ClothAudit, Long>, JpaSpecificationExecutor<ClothAudit> {}
