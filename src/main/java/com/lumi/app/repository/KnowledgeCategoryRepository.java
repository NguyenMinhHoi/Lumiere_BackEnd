package com.lumi.app.repository;

import com.lumi.app.domain.KnowledgeCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KnowledgeCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {}
