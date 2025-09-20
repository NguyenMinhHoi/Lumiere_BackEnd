package com.lumi.app.web.rest;

import com.lumi.app.repository.ClothProductMapRepository;
import com.lumi.app.service.ClothProductMapQueryService;
import com.lumi.app.service.ClothProductMapService;
import com.lumi.app.service.criteria.ClothProductMapCriteria;
import com.lumi.app.service.dto.ClothProductMapDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.ClothProductMap}.
 */
@RestController
@RequestMapping("/api/cloth-product-maps")
public class ClothProductMapResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClothProductMapResource.class);

    private static final String ENTITY_NAME = "clothProductMap";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClothProductMapService clothProductMapService;

    private final ClothProductMapRepository clothProductMapRepository;

    private final ClothProductMapQueryService clothProductMapQueryService;

    public ClothProductMapResource(
        ClothProductMapService clothProductMapService,
        ClothProductMapRepository clothProductMapRepository,
        ClothProductMapQueryService clothProductMapQueryService
    ) {
        this.clothProductMapService = clothProductMapService;
        this.clothProductMapRepository = clothProductMapRepository;
        this.clothProductMapQueryService = clothProductMapQueryService;
    }

    /**
     * {@code POST  /cloth-product-maps} : Create a new clothProductMap.
     *
     * @param clothProductMapDTO the clothProductMapDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clothProductMapDTO, or with status {@code 400 (Bad Request)} if the clothProductMap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClothProductMapDTO> createClothProductMap(@Valid @RequestBody ClothProductMapDTO clothProductMapDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClothProductMap : {}", clothProductMapDTO);
        if (clothProductMapDTO.getId() != null) {
            throw new BadRequestAlertException("A new clothProductMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clothProductMapDTO = clothProductMapService.save(clothProductMapDTO);
        return ResponseEntity.created(new URI("/api/cloth-product-maps/" + clothProductMapDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clothProductMapDTO.getId().toString()))
            .body(clothProductMapDTO);
    }

    /**
     * {@code PUT  /cloth-product-maps/:id} : Updates an existing clothProductMap.
     *
     * @param id the id of the clothProductMapDTO to save.
     * @param clothProductMapDTO the clothProductMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothProductMapDTO,
     * or with status {@code 400 (Bad Request)} if the clothProductMapDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clothProductMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClothProductMapDTO> updateClothProductMap(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClothProductMapDTO clothProductMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClothProductMap : {}, {}", id, clothProductMapDTO);
        if (clothProductMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothProductMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothProductMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clothProductMapDTO = clothProductMapService.update(clothProductMapDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothProductMapDTO.getId().toString()))
            .body(clothProductMapDTO);
    }

    /**
     * {@code PATCH  /cloth-product-maps/:id} : Partial updates given fields of an existing clothProductMap, field will ignore if it is null
     *
     * @param id the id of the clothProductMapDTO to save.
     * @param clothProductMapDTO the clothProductMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothProductMapDTO,
     * or with status {@code 400 (Bad Request)} if the clothProductMapDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clothProductMapDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clothProductMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClothProductMapDTO> partialUpdateClothProductMap(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClothProductMapDTO clothProductMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClothProductMap partially : {}, {}", id, clothProductMapDTO);
        if (clothProductMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothProductMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothProductMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClothProductMapDTO> result = clothProductMapService.partialUpdate(clothProductMapDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothProductMapDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cloth-product-maps} : get all the clothProductMaps.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clothProductMaps in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClothProductMapDTO>> getAllClothProductMaps(
        ClothProductMapCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ClothProductMaps by criteria: {}", criteria);

        Page<ClothProductMapDTO> page = clothProductMapQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cloth-product-maps/count} : count all the clothProductMaps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClothProductMaps(ClothProductMapCriteria criteria) {
        LOG.debug("REST request to count ClothProductMaps by criteria: {}", criteria);
        return ResponseEntity.ok().body(clothProductMapQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cloth-product-maps/:id} : get the "id" clothProductMap.
     *
     * @param id the id of the clothProductMapDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clothProductMapDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClothProductMapDTO> getClothProductMap(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClothProductMap : {}", id);
        Optional<ClothProductMapDTO> clothProductMapDTO = clothProductMapService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clothProductMapDTO);
    }

    /**
     * {@code DELETE  /cloth-product-maps/:id} : delete the "id" clothProductMap.
     *
     * @param id the id of the clothProductMapDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothProductMap(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClothProductMap : {}", id);
        clothProductMapService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cloth-product-maps/_search?query=:query} : search for the clothProductMap corresponding
     * to the query.
     *
     * @param query the query of the clothProductMap search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ClothProductMapDTO>> searchClothProductMaps(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ClothProductMaps for query {}", query);
        try {
            Page<ClothProductMapDTO> page = clothProductMapService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
