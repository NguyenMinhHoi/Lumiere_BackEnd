package com.lumi.app.service;

import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.app.domain.KnowledgeCategory}.
 */
public interface KnowledgeCategoryService {
    /**
     * Save a knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    KnowledgeCategoryDTO save(KnowledgeCategoryDTO knowledgeCategoryDTO);

    /**
     * Updates a knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    KnowledgeCategoryDTO update(KnowledgeCategoryDTO knowledgeCategoryDTO);

    /**
     * Partially updates a knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KnowledgeCategoryDTO> partialUpdate(KnowledgeCategoryDTO knowledgeCategoryDTO);

    /**
     * Get all the knowledgeCategories.
     *
     * @return the list of entities.
     */
    List<KnowledgeCategoryDTO> findAll();

    /**
     * Get the "id" knowledgeCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KnowledgeCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" knowledgeCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the knowledgeCategory corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<KnowledgeCategoryDTO> search(String query);
}
