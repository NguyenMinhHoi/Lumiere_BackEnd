package com.lumi.app.web.rest;

import com.lumi.app.repository.ClothAuditRepository;
import com.lumi.app.service.ClothAuditQueryService;
import com.lumi.app.service.ClothAuditService;
import com.lumi.app.service.criteria.ClothAuditCriteria;
import com.lumi.app.service.dto.ClothAuditDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.ClothAudit}.
 */
@RestController
@RequestMapping("/api/cloth-audits")
public class ClothAuditResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClothAuditResource.class);

    private static final String ENTITY_NAME = "clothAudit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClothAuditService clothAuditService;

    private final ClothAuditRepository clothAuditRepository;

    private final ClothAuditQueryService clothAuditQueryService;

    public ClothAuditResource(
        ClothAuditService clothAuditService,
        ClothAuditRepository clothAuditRepository,
        ClothAuditQueryService clothAuditQueryService
    ) {
        this.clothAuditService = clothAuditService;
        this.clothAuditRepository = clothAuditRepository;
        this.clothAuditQueryService = clothAuditQueryService;
    }

    /**
     * {@code POST  /cloth-audits} : Create a new clothAudit.
     *
     * @param clothAuditDTO the clothAuditDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clothAuditDTO, or with status {@code 400 (Bad Request)} if the clothAudit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClothAuditDTO> createClothAudit(@Valid @RequestBody ClothAuditDTO clothAuditDTO) throws URISyntaxException {
        LOG.debug("REST request to save ClothAudit : {}", clothAuditDTO);
        if (clothAuditDTO.getId() != null) {
            throw new BadRequestAlertException("A new clothAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clothAuditDTO = clothAuditService.save(clothAuditDTO);
        return ResponseEntity.created(new URI("/api/cloth-audits/" + clothAuditDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clothAuditDTO.getId().toString()))
            .body(clothAuditDTO);
    }

    /**
     * {@code PUT  /cloth-audits/:id} : Updates an existing clothAudit.
     *
     * @param id the id of the clothAuditDTO to save.
     * @param clothAuditDTO the clothAuditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothAuditDTO,
     * or with status {@code 400 (Bad Request)} if the clothAuditDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clothAuditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClothAuditDTO> updateClothAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClothAuditDTO clothAuditDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClothAudit : {}, {}", id, clothAuditDTO);
        if (clothAuditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothAuditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clothAuditDTO = clothAuditService.update(clothAuditDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothAuditDTO.getId().toString()))
            .body(clothAuditDTO);
    }

    /**
     * {@code PATCH  /cloth-audits/:id} : Partial updates given fields of an existing clothAudit, field will ignore if it is null
     *
     * @param id the id of the clothAuditDTO to save.
     * @param clothAuditDTO the clothAuditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothAuditDTO,
     * or with status {@code 400 (Bad Request)} if the clothAuditDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clothAuditDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clothAuditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClothAuditDTO> partialUpdateClothAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClothAuditDTO clothAuditDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClothAudit partially : {}, {}", id, clothAuditDTO);
        if (clothAuditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothAuditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClothAuditDTO> result = clothAuditService.partialUpdate(clothAuditDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothAuditDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cloth-audits} : get all the clothAudits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clothAudits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClothAuditDTO>> getAllClothAudits(
        ClothAuditCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ClothAudits by criteria: {}", criteria);

        Page<ClothAuditDTO> page = clothAuditQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cloth-audits/count} : count all the clothAudits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClothAudits(ClothAuditCriteria criteria) {
        LOG.debug("REST request to count ClothAudits by criteria: {}", criteria);
        return ResponseEntity.ok().body(clothAuditQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cloth-audits/:id} : get the "id" clothAudit.
     *
     * @param id the id of the clothAuditDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clothAuditDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClothAuditDTO> getClothAudit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClothAudit : {}", id);
        Optional<ClothAuditDTO> clothAuditDTO = clothAuditService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clothAuditDTO);
    }

    /**
     * {@code DELETE  /cloth-audits/:id} : delete the "id" clothAudit.
     *
     * @param id the id of the clothAuditDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothAudit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClothAudit : {}", id);
        clothAuditService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cloth-audits/_search?query=:query} : search for the clothAudit corresponding
     * to the query.
     *
     * @param query the query of the clothAudit search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ClothAuditDTO>> searchClothAudits(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ClothAudits for query {}", query);
        try {
            Page<ClothAuditDTO> page = clothAuditService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
