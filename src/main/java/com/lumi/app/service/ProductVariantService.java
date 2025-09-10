package com.lumi.app.service;

import com.lumi.app.domain.ProductVariant;
import com.lumi.app.repository.ProductVariantRepository;
import com.lumi.app.repository.search.ProductVariantSearchRepository;
import com.lumi.app.service.dto.ProductVariantDTO;
import com.lumi.app.service.mapper.ProductVariantMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.ProductVariant}.
 */
@Service
@Transactional
public class ProductVariantService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductVariantService.class);

    private final ProductVariantRepository productVariantRepository;

    private final ProductVariantMapper productVariantMapper;

    private final ProductVariantSearchRepository productVariantSearchRepository;

    public ProductVariantService(
        ProductVariantRepository productVariantRepository,
        ProductVariantMapper productVariantMapper,
        ProductVariantSearchRepository productVariantSearchRepository
    ) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
        this.productVariantSearchRepository = productVariantSearchRepository;
    }

    /**
     * Save a productVariant.
     *
     * @param productVariantDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductVariantDTO save(ProductVariantDTO productVariantDTO) {
        LOG.debug("Request to save ProductVariant : {}", productVariantDTO);
        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDTO);
        productVariant = productVariantRepository.save(productVariant);
        productVariantSearchRepository.index(productVariant);
        return productVariantMapper.toDto(productVariant);
    }

    /**
     * Update a productVariant.
     *
     * @param productVariantDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductVariantDTO update(ProductVariantDTO productVariantDTO) {
        LOG.debug("Request to update ProductVariant : {}", productVariantDTO);
        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDTO);
        productVariant = productVariantRepository.save(productVariant);
        productVariantSearchRepository.index(productVariant);
        return productVariantMapper.toDto(productVariant);
    }

    /**
     * Partially update a productVariant.
     *
     * @param productVariantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductVariantDTO> partialUpdate(ProductVariantDTO productVariantDTO) {
        LOG.debug("Request to partially update ProductVariant : {}", productVariantDTO);

        return productVariantRepository
            .findById(productVariantDTO.getId())
            .map(existingProductVariant -> {
                productVariantMapper.partialUpdate(existingProductVariant, productVariantDTO);

                return existingProductVariant;
            })
            .map(productVariantRepository::save)
            .map(savedProductVariant -> {
                productVariantSearchRepository.index(savedProductVariant);
                return savedProductVariant;
            })
            .map(productVariantMapper::toDto);
    }

    /**
     * Get all the productVariants with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductVariantDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productVariantRepository.findAllWithEagerRelationships(pageable).map(productVariantMapper::toDto);
    }

    /**
     * Get one productVariant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductVariantDTO> findOne(Long id) {
        LOG.debug("Request to get ProductVariant : {}", id);
        return productVariantRepository.findOneWithEagerRelationships(id).map(productVariantMapper::toDto);
    }

    /**
     * Delete the productVariant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProductVariant : {}", id);
        productVariantRepository.deleteById(id);
        productVariantSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the productVariant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductVariantDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ProductVariants for query {}", query);
        return productVariantSearchRepository.search(query, pageable).map(productVariantMapper::toDto);
    }
}
