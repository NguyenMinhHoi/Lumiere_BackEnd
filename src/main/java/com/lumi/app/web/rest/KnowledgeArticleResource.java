package com.lumi.app.web.rest;

import com.lumi.app.repository.KnowledgeArticleRepository;
import com.lumi.app.service.KnowledgeArticleQueryService;
import com.lumi.app.service.KnowledgeArticleService;
import com.lumi.app.service.criteria.KnowledgeArticleCriteria;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.app.domain.KnowledgeArticle}.
 */
@RestController
@RequestMapping("/api/knowledge-articles")
public class KnowledgeArticleResource {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeArticleResource.class);

    private static final String ENTITY_NAME = "knowledgeArticle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KnowledgeArticleService knowledgeArticleService;

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    private final KnowledgeArticleQueryService knowledgeArticleQueryService;

    public KnowledgeArticleResource(
        KnowledgeArticleService knowledgeArticleService,
        KnowledgeArticleRepository knowledgeArticleRepository,
        KnowledgeArticleQueryService knowledgeArticleQueryService
    ) {
        this.knowledgeArticleService = knowledgeArticleService;
        this.knowledgeArticleRepository = knowledgeArticleRepository;
        this.knowledgeArticleQueryService = knowledgeArticleQueryService;
    }

    /**
     * {@code POST  /knowledge-articles} : Create a new knowledgeArticle.
     *
     * @param knowledgeArticleDTO the knowledgeArticleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new knowledgeArticleDTO, or with status {@code 400 (Bad Request)} if the knowledgeArticle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KnowledgeArticleDTO> createKnowledgeArticle(@Valid @RequestBody KnowledgeArticleDTO knowledgeArticleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save KnowledgeArticle : {}", knowledgeArticleDTO);
        if (knowledgeArticleDTO.getId() != null) {
            throw new BadRequestAlertException("A new knowledgeArticle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        knowledgeArticleDTO = knowledgeArticleService.save(knowledgeArticleDTO);
        return ResponseEntity.created(new URI("/api/knowledge-articles/" + knowledgeArticleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, knowledgeArticleDTO.getId().toString()))
            .body(knowledgeArticleDTO);
    }

    /**
     * {@code PUT  /knowledge-articles/:id} : Updates an existing knowledgeArticle.
     *
     * @param id the id of the knowledgeArticleDTO to save.
     * @param knowledgeArticleDTO the knowledgeArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeArticleDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeArticleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeArticleDTO> updateKnowledgeArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody KnowledgeArticleDTO knowledgeArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KnowledgeArticle : {}, {}", id, knowledgeArticleDTO);
        if (knowledgeArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        knowledgeArticleDTO = knowledgeArticleService.update(knowledgeArticleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeArticleDTO.getId().toString()))
            .body(knowledgeArticleDTO);
    }

    /**
     * {@code PATCH  /knowledge-articles/:id} : Partial updates given fields of an existing knowledgeArticle, field will ignore if it is null
     *
     * @param id the id of the knowledgeArticleDTO to save.
     * @param knowledgeArticleDTO the knowledgeArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeArticleDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeArticleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the knowledgeArticleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KnowledgeArticleDTO> partialUpdateKnowledgeArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KnowledgeArticleDTO knowledgeArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update KnowledgeArticle partially : {}, {}", id, knowledgeArticleDTO);
        if (knowledgeArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KnowledgeArticleDTO> result = knowledgeArticleService.partialUpdate(knowledgeArticleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeArticleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /knowledge-articles} : get all the knowledgeArticles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of knowledgeArticles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KnowledgeArticleDTO>> getAllKnowledgeArticles(
        KnowledgeArticleCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get KnowledgeArticles by criteria: {}", criteria);

        Page<KnowledgeArticleDTO> page = knowledgeArticleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /knowledge-articles/count} : count all the knowledgeArticles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countKnowledgeArticles(KnowledgeArticleCriteria criteria) {
        LOG.debug("REST request to count KnowledgeArticles by criteria: {}", criteria);
        return ResponseEntity.ok().body(knowledgeArticleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /knowledge-articles/:id} : get the "id" knowledgeArticle.
     *
     * @param id the id of the knowledgeArticleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the knowledgeArticleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeArticleDTO> getKnowledgeArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KnowledgeArticle : {}", id);
        Optional<KnowledgeArticleDTO> knowledgeArticleDTO = knowledgeArticleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledgeArticleDTO);
    }

    /**
     * {@code DELETE  /knowledge-articles/:id} : delete the "id" knowledgeArticle.
     *
     * @param id the id of the knowledgeArticleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KnowledgeArticle : {}", id);
        knowledgeArticleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /knowledge-articles/_search?query=:query} : search for the knowledgeArticle corresponding
     * to the query.
     *
     * @param query the query of the knowledgeArticle search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<KnowledgeArticleDTO>> searchKnowledgeArticles(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of KnowledgeArticles for query {}", query);
        try {
            Page<KnowledgeArticleDTO> page = knowledgeArticleService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
