package com.lumi.app.repository;

import com.lumi.app.domain.ArticleTag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ArticleTag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {}
