package com.lumi.app.repository;

import com.lumi.app.domain.KnowledgeArticle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class KnowledgeArticleRepositoryWithBagRelationshipsImpl implements KnowledgeArticleRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String KNOWLEDGEARTICLES_PARAMETER = "knowledgeArticles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<KnowledgeArticle> fetchBagRelationships(Optional<KnowledgeArticle> knowledgeArticle) {
        return knowledgeArticle.map(this::fetchTags);
    }

    @Override
    public Page<KnowledgeArticle> fetchBagRelationships(Page<KnowledgeArticle> knowledgeArticles) {
        return new PageImpl<>(
            fetchBagRelationships(knowledgeArticles.getContent()),
            knowledgeArticles.getPageable(),
            knowledgeArticles.getTotalElements()
        );
    }

    @Override
    public List<KnowledgeArticle> fetchBagRelationships(List<KnowledgeArticle> knowledgeArticles) {
        return Optional.of(knowledgeArticles).map(this::fetchTags).orElse(Collections.emptyList());
    }

    KnowledgeArticle fetchTags(KnowledgeArticle result) {
        return entityManager
            .createQuery(
                "select knowledgeArticle from KnowledgeArticle knowledgeArticle left join fetch knowledgeArticle.tags where knowledgeArticle.id = :id",
                KnowledgeArticle.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<KnowledgeArticle> fetchTags(List<KnowledgeArticle> knowledgeArticles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, knowledgeArticles.size()).forEach(index -> order.put(knowledgeArticles.get(index).getId(), index));
        List<KnowledgeArticle> result = entityManager
            .createQuery(
                "select knowledgeArticle from KnowledgeArticle knowledgeArticle left join fetch knowledgeArticle.tags where knowledgeArticle in :knowledgeArticles",
                KnowledgeArticle.class
            )
            .setParameter(KNOWLEDGEARTICLES_PARAMETER, knowledgeArticles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
