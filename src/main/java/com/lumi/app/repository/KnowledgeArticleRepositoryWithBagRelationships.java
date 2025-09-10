package com.lumi.app.repository;

import com.lumi.app.domain.KnowledgeArticle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface KnowledgeArticleRepositoryWithBagRelationships {
    Optional<KnowledgeArticle> fetchBagRelationships(Optional<KnowledgeArticle> knowledgeArticle);

    List<KnowledgeArticle> fetchBagRelationships(List<KnowledgeArticle> knowledgeArticles);

    Page<KnowledgeArticle> fetchBagRelationships(Page<KnowledgeArticle> knowledgeArticles);
}
