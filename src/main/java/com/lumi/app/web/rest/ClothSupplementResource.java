package com.lumi.app.web.rest;

import com.lumi.app.repository.ClothSupplementRepository;
import com.lumi.app.service.ClothSupplementQueryService;
import com.lumi.app.service.ClothSupplementService;
import com.lumi.app.service.criteria.ClothSupplementCriteria;
import com.lumi.app.service.dto.ClothSupplementDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.ClothSupplement}.
 */
@RestController
@RequestMapping("/api/cloth-supplements")
public class ClothSupplementResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClothSupplementResource.class);

    private static final String ENTITY_NAME = "clothSupplement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClothSupplementService clothSupplementService;

    private final ClothSupplementRepository clothSupplementRepository;

    private final ClothSupplementQueryService clothSupplementQueryService;

    public ClothSupplementResource(
        ClothSupplementService clothSupplementService,
        ClothSupplementRepository clothSupplementRepository,
        ClothSupplementQueryService clothSupplementQueryService
    ) {
        this.clothSupplementService = clothSupplementService;
        this.clothSupplementRepository = clothSupplementRepository;
        this.clothSupplementQueryService = clothSupplementQueryService;
    }

    /**
     * {@code POST  /cloth-supplements} : Create a new clothSupplement.
     *
     * @param clothSupplementDTO the clothSupplementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clothSupplementDTO, or with status {@code 400 (Bad Request)} if the clothSupplement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClothSupplementDTO> createClothSupplement(@Valid @RequestBody ClothSupplementDTO clothSupplementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClothSupplement : {}", clothSupplementDTO);
        if (clothSupplementDTO.getId() != null) {
            throw new BadRequestAlertException("A new clothSupplement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clothSupplementDTO = clothSupplementService.save(clothSupplementDTO);
        return ResponseEntity.created(new URI("/api/cloth-supplements/" + clothSupplementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clothSupplementDTO.getId().toString()))
            .body(clothSupplementDTO);
    }

    /**
     * {@code PUT  /cloth-supplements/:id} : Updates an existing clothSupplement.
     *
     * @param id the id of the clothSupplementDTO to save.
     * @param clothSupplementDTO the clothSupplementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothSupplementDTO,
     * or with status {@code 400 (Bad Request)} if the clothSupplementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clothSupplementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClothSupplementDTO> updateClothSupplement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClothSupplementDTO clothSupplementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClothSupplement : {}, {}", id, clothSupplementDTO);
        if (clothSupplementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothSupplementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothSupplementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clothSupplementDTO = clothSupplementService.update(clothSupplementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothSupplementDTO.getId().toString()))
            .body(clothSupplementDTO);
    }

    /**
     * {@code PATCH  /cloth-supplements/:id} : Partial updates given fields of an existing clothSupplement, field will ignore if it is null
     *
     * @param id the id of the clothSupplementDTO to save.
     * @param clothSupplementDTO the clothSupplementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothSupplementDTO,
     * or with status {@code 400 (Bad Request)} if the clothSupplementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clothSupplementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clothSupplementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClothSupplementDTO> partialUpdateClothSupplement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClothSupplementDTO clothSupplementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClothSupplement partially : {}, {}", id, clothSupplementDTO);
        if (clothSupplementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothSupplementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothSupplementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClothSupplementDTO> result = clothSupplementService.partialUpdate(clothSupplementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothSupplementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cloth-supplements} : get all the clothSupplements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clothSupplements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClothSupplementDTO>> getAllClothSupplements(
        ClothSupplementCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ClothSupplements by criteria: {}", criteria);

        Page<ClothSupplementDTO> page = clothSupplementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cloth-supplements/count} : count all the clothSupplements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClothSupplements(ClothSupplementCriteria criteria) {
        LOG.debug("REST request to count ClothSupplements by criteria: {}", criteria);
        return ResponseEntity.ok().body(clothSupplementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cloth-supplements/:id} : get the "id" clothSupplement.
     *
     * @param id the id of the clothSupplementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clothSupplementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClothSupplementDTO> getClothSupplement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClothSupplement : {}", id);
        Optional<ClothSupplementDTO> clothSupplementDTO = clothSupplementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clothSupplementDTO);
    }

    /**
     * {@code DELETE  /cloth-supplements/:id} : delete the "id" clothSupplement.
     *
     * @param id the id of the clothSupplementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothSupplement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClothSupplement : {}", id);
        clothSupplementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cloth-supplements/_search?query=:query} : search for the clothSupplement corresponding
     * to the query.
     *
     * @param query the query of the clothSupplement search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ClothSupplementDTO>> searchClothSupplements(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ClothSupplements for query {}", query);
        try {
            Page<ClothSupplementDTO> page = clothSupplementService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
