package com.lumi.app.web.rest;

import com.lumi.app.repository.KnowledgeCategoryRepository;
import com.lumi.app.service.KnowledgeCategoryService;
import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import com.lumi.app.web.rest.errors.BadRequestAlertException;
import com.lumi.app.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.app.domain.KnowledgeCategory}.
 */
@RestController
@RequestMapping("/api/knowledge-categories")
public class KnowledgeCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeCategoryResource.class);

    private static final String ENTITY_NAME = "knowledgeCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KnowledgeCategoryService knowledgeCategoryService;

    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    public KnowledgeCategoryResource(
        KnowledgeCategoryService knowledgeCategoryService,
        KnowledgeCategoryRepository knowledgeCategoryRepository
    ) {
        this.knowledgeCategoryService = knowledgeCategoryService;
        this.knowledgeCategoryRepository = knowledgeCategoryRepository;
    }

    /**
     * {@code POST  /knowledge-categories} : Create a new knowledgeCategory.
     *
     * @param knowledgeCategoryDTO the knowledgeCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new knowledgeCategoryDTO, or with status {@code 400 (Bad Request)} if the knowledgeCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KnowledgeCategoryDTO> createKnowledgeCategory(@Valid @RequestBody KnowledgeCategoryDTO knowledgeCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save KnowledgeCategory : {}", knowledgeCategoryDTO);
        if (knowledgeCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new knowledgeCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        knowledgeCategoryDTO = knowledgeCategoryService.save(knowledgeCategoryDTO);
        return ResponseEntity.created(new URI("/api/knowledge-categories/" + knowledgeCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, knowledgeCategoryDTO.getId().toString()))
            .body(knowledgeCategoryDTO);
    }

    /**
     * {@code PUT  /knowledge-categories/:id} : Updates an existing knowledgeCategory.
     *
     * @param id the id of the knowledgeCategoryDTO to save.
     * @param knowledgeCategoryDTO the knowledgeCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeCategoryDTO> updateKnowledgeCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody KnowledgeCategoryDTO knowledgeCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KnowledgeCategory : {}, {}", id, knowledgeCategoryDTO);
        if (knowledgeCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        knowledgeCategoryDTO = knowledgeCategoryService.update(knowledgeCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeCategoryDTO.getId().toString()))
            .body(knowledgeCategoryDTO);
    }

    /**
     * {@code PATCH  /knowledge-categories/:id} : Partial updates given fields of an existing knowledgeCategory, field will ignore if it is null
     *
     * @param id the id of the knowledgeCategoryDTO to save.
     * @param knowledgeCategoryDTO the knowledgeCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the knowledgeCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KnowledgeCategoryDTO> partialUpdateKnowledgeCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KnowledgeCategoryDTO knowledgeCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update KnowledgeCategory partially : {}, {}", id, knowledgeCategoryDTO);
        if (knowledgeCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KnowledgeCategoryDTO> result = knowledgeCategoryService.partialUpdate(knowledgeCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /knowledge-categories} : get all the knowledgeCategories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of knowledgeCategories in body.
     */
    @GetMapping("")
    public List<KnowledgeCategoryDTO> getAllKnowledgeCategories() {
        LOG.debug("REST request to get all KnowledgeCategories");
        return knowledgeCategoryService.findAll();
    }

    /**
     * {@code GET  /knowledge-categories/:id} : get the "id" knowledgeCategory.
     *
     * @param id the id of the knowledgeCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the knowledgeCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeCategoryDTO> getKnowledgeCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KnowledgeCategory : {}", id);
        Optional<KnowledgeCategoryDTO> knowledgeCategoryDTO = knowledgeCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledgeCategoryDTO);
    }

    /**
     * {@code DELETE  /knowledge-categories/:id} : delete the "id" knowledgeCategory.
     *
     * @param id the id of the knowledgeCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KnowledgeCategory : {}", id);
        knowledgeCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /knowledge-categories/_search?query=:query} : search for the knowledgeCategory corresponding
     * to the query.
     *
     * @param query the query of the knowledgeCategory search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<KnowledgeCategoryDTO> searchKnowledgeCategories(@RequestParam("query") String query) {
        LOG.debug("REST request to search KnowledgeCategories for query {}", query);
        try {
            return knowledgeCategoryService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
