package com.lumi.app.repository;

import com.lumi.app.domain.CompanyConfigAdditional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CompanyConfigAdditional entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyConfigAdditionalRepository extends JpaRepository<CompanyConfigAdditional, Long> {}
