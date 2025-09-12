package com.lumi.app.web.rest;

import com.lumi.app.repository.SupplementRepository;
import com.lumi.app.service.SupplementQueryService;
import com.lumi.app.service.SupplementService;
import com.lumi.app.service.criteria.SupplementCriteria;
import com.lumi.app.service.dto.SupplementDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.Supplement}.
 */
@RestController
@RequestMapping("/api/supplements")
public class SupplementResource {

    private static final Logger LOG = LoggerFactory.getLogger(SupplementResource.class);

    private static final String ENTITY_NAME = "supplement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplementService supplementService;

    private final SupplementRepository supplementRepository;

    private final SupplementQueryService supplementQueryService;

    public SupplementResource(
        SupplementService supplementService,
        SupplementRepository supplementRepository,
        SupplementQueryService supplementQueryService
    ) {
        this.supplementService = supplementService;
        this.supplementRepository = supplementRepository;
        this.supplementQueryService = supplementQueryService;
    }

    /**
     * {@code POST  /supplements} : Create a new supplement.
     *
     * @param supplementDTO the supplementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplementDTO, or with status {@code 400 (Bad Request)} if the supplement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SupplementDTO> createSupplement(@Valid @RequestBody SupplementDTO supplementDTO) throws URISyntaxException {
        LOG.debug("REST request to save Supplement : {}", supplementDTO);
        if (supplementDTO.getId() != null) {
            throw new BadRequestAlertException("A new supplement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        supplementDTO = supplementService.save(supplementDTO);
        return ResponseEntity.created(new URI("/api/supplements/" + supplementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, supplementDTO.getId().toString()))
            .body(supplementDTO);
    }

    /**
     * {@code PUT  /supplements/:id} : Updates an existing supplement.
     *
     * @param id the id of the supplementDTO to save.
     * @param supplementDTO the supplementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplementDTO,
     * or with status {@code 400 (Bad Request)} if the supplementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supplementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplementDTO> updateSupplement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplementDTO supplementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Supplement : {}, {}", id, supplementDTO);
        if (supplementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        supplementDTO = supplementService.update(supplementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplementDTO.getId().toString()))
            .body(supplementDTO);
    }

    /**
     * {@code PATCH  /supplements/:id} : Partial updates given fields of an existing supplement, field will ignore if it is null
     *
     * @param id the id of the supplementDTO to save.
     * @param supplementDTO the supplementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplementDTO,
     * or with status {@code 400 (Bad Request)} if the supplementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the supplementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the supplementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupplementDTO> partialUpdateSupplement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupplementDTO supplementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Supplement partially : {}, {}", id, supplementDTO);
        if (supplementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupplementDTO> result = supplementService.partialUpdate(supplementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /supplements} : get all the supplements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supplements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SupplementDTO>> getAllSupplements(
        SupplementCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Supplements by criteria: {}", criteria);

        Page<SupplementDTO> page = supplementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /supplements/count} : count all the supplements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSupplements(SupplementCriteria criteria) {
        LOG.debug("REST request to count Supplements by criteria: {}", criteria);
        return ResponseEntity.ok().body(supplementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /supplements/:id} : get the "id" supplement.
     *
     * @param id the id of the supplementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplementDTO> getSupplement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Supplement : {}", id);
        Optional<SupplementDTO> supplementDTO = supplementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(supplementDTO);
    }

    /**
     * {@code DELETE  /supplements/:id} : delete the "id" supplement.
     *
     * @param id the id of the supplementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Supplement : {}", id);
        supplementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /supplements/_search?query=:query} : search for the supplement corresponding
     * to the query.
     *
     * @param query the query of the supplement search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SupplementDTO>> searchSupplements(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Supplements for query {}", query);
        try {
            Page<SupplementDTO> page = supplementService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
