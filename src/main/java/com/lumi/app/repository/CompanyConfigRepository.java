package com.lumi.app.repository;

import com.lumi.app.domain.CompanyConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CompanyConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyConfigRepository extends JpaRepository<CompanyConfig, Long> {}
