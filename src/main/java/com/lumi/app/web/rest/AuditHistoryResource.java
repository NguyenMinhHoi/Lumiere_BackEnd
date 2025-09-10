package com.lumi.app.web.rest;

import com.lumi.app.repository.AuditHistoryRepository;
import com.lumi.app.service.AuditHistoryQueryService;
import com.lumi.app.service.AuditHistoryService;
import com.lumi.app.service.criteria.AuditHistoryCriteria;
import com.lumi.app.service.dto.AuditHistoryDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.AuditHistory}.
 */
@RestController
@RequestMapping("/api/audit-histories")
public class AuditHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditHistoryResource.class);

    private static final String ENTITY_NAME = "auditHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuditHistoryService auditHistoryService;

    private final AuditHistoryRepository auditHistoryRepository;

    private final AuditHistoryQueryService auditHistoryQueryService;

    public AuditHistoryResource(
        AuditHistoryService auditHistoryService,
        AuditHistoryRepository auditHistoryRepository,
        AuditHistoryQueryService auditHistoryQueryService
    ) {
        this.auditHistoryService = auditHistoryService;
        this.auditHistoryRepository = auditHistoryRepository;
        this.auditHistoryQueryService = auditHistoryQueryService;
    }

    /**
     * {@code POST  /audit-histories} : Create a new auditHistory.
     *
     * @param auditHistoryDTO the auditHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditHistoryDTO, or with status {@code 400 (Bad Request)} if the auditHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AuditHistoryDTO> createAuditHistory(@Valid @RequestBody AuditHistoryDTO auditHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditHistory : {}", auditHistoryDTO);
        if (auditHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        auditHistoryDTO = auditHistoryService.save(auditHistoryDTO);
        return ResponseEntity.created(new URI("/api/audit-histories/" + auditHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, auditHistoryDTO.getId().toString()))
            .body(auditHistoryDTO);
    }

    /**
     * {@code PUT  /audit-histories/:id} : Updates an existing auditHistory.
     *
     * @param id the id of the auditHistoryDTO to save.
     * @param auditHistoryDTO the auditHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the auditHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuditHistoryDTO> updateAuditHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditHistoryDTO auditHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditHistory : {}, {}", id, auditHistoryDTO);
        if (auditHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        auditHistoryDTO = auditHistoryService.update(auditHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, auditHistoryDTO.getId().toString()))
            .body(auditHistoryDTO);
    }

    /**
     * {@code PATCH  /audit-histories/:id} : Partial updates given fields of an existing auditHistory, field will ignore if it is null
     *
     * @param id the id of the auditHistoryDTO to save.
     * @param auditHistoryDTO the auditHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the auditHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AuditHistoryDTO> partialUpdateAuditHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditHistoryDTO auditHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AuditHistory partially : {}, {}", id, auditHistoryDTO);
        if (auditHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuditHistoryDTO> result = auditHistoryService.partialUpdate(auditHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, auditHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /audit-histories} : get all the auditHistories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auditHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AuditHistoryDTO>> getAllAuditHistories(
        AuditHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AuditHistories by criteria: {}", criteria);

        Page<AuditHistoryDTO> page = auditHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /audit-histories/count} : count all the auditHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAuditHistories(AuditHistoryCriteria criteria) {
        LOG.debug("REST request to count AuditHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(auditHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /audit-histories/:id} : get the "id" auditHistory.
     *
     * @param id the id of the auditHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditHistoryDTO> getAuditHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditHistory : {}", id);
        Optional<AuditHistoryDTO> auditHistoryDTO = auditHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditHistoryDTO);
    }

    /**
     * {@code DELETE  /audit-histories/:id} : delete the "id" auditHistory.
     *
     * @param id the id of the auditHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditHistory : {}", id);
        auditHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /audit-histories/_search?query=:query} : search for the auditHistory corresponding
     * to the query.
     *
     * @param query the query of the auditHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<AuditHistoryDTO>> searchAuditHistories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of AuditHistories for query {}", query);
        try {
            Page<AuditHistoryDTO> page = auditHistoryService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
