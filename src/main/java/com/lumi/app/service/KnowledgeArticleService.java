package com.lumi.app.service;

import com.lumi.app.service.dto.KnowledgeArticleDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.app.domain.KnowledgeArticle}.
 */
public interface KnowledgeArticleService {
    /**
     * Save a knowledgeArticle.
     *
     * @param knowledgeArticleDTO the entity to save.
     * @return the persisted entity.
     */
    KnowledgeArticleDTO save(KnowledgeArticleDTO knowledgeArticleDTO);

    /**
     * Updates a knowledgeArticle.
     *
     * @param knowledgeArticleDTO the entity to update.
     * @return the persisted entity.
     */
    KnowledgeArticleDTO update(KnowledgeArticleDTO knowledgeArticleDTO);

    /**
     * Partially updates a knowledgeArticle.
     *
     * @param knowledgeArticleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KnowledgeArticleDTO> partialUpdate(KnowledgeArticleDTO knowledgeArticleDTO);

    /**
     * Get the "id" knowledgeArticle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KnowledgeArticleDTO> findOne(Long id);

    /**
     * Delete the "id" knowledgeArticle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the knowledgeArticle corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<KnowledgeArticleDTO> search(String query, Pageable pageable);
}
