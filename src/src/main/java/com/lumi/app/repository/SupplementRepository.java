package com.lumi.app.repository;

import com.lumi.app.domain.Supplement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Supplement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SupplementRepository extends JpaRepository<Supplement, Long>, JpaSpecificationExecutor<Supplement> {}
