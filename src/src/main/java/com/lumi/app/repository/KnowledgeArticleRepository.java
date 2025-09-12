package com.lumi.app.repository;

import com.lumi.app.domain.KnowledgeArticle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KnowledgeArticle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long>, JpaSpecificationExecutor<KnowledgeArticle> {}
