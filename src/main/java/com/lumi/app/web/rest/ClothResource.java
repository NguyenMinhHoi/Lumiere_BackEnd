package com.lumi.app.web.rest;

import com.lumi.app.repository.ClothRepository;
import com.lumi.app.service.ClothQueryService;
import com.lumi.app.service.ClothService;
import com.lumi.app.service.criteria.ClothCriteria;
import com.lumi.app.service.dto.ClothDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.Cloth}.
 */
@RestController
@RequestMapping("/api/cloths")
public class ClothResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClothResource.class);

    private static final String ENTITY_NAME = "cloth";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClothService clothService;

    private final ClothRepository clothRepository;

    private final ClothQueryService clothQueryService;

    public ClothResource(ClothService clothService, ClothRepository clothRepository, ClothQueryService clothQueryService) {
        this.clothService = clothService;
        this.clothRepository = clothRepository;
        this.clothQueryService = clothQueryService;
    }

    /**
     * {@code POST  /cloths} : Create a new cloth.
     *
     * @param clothDTO the clothDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clothDTO, or with status {@code 400 (Bad Request)} if the cloth has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClothDTO> createCloth(@Valid @RequestBody ClothDTO clothDTO) throws URISyntaxException {
        LOG.debug("REST request to save Cloth : {}", clothDTO);
        if (clothDTO.getId() != null) {
            throw new BadRequestAlertException("A new cloth cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clothDTO = clothService.save(clothDTO);
        return ResponseEntity.created(new URI("/api/cloths/" + clothDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clothDTO.getId().toString()))
            .body(clothDTO);
    }

    /**
     * {@code PUT  /cloths/:id} : Updates an existing cloth.
     *
     * @param id the id of the clothDTO to save.
     * @param clothDTO the clothDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothDTO,
     * or with status {@code 400 (Bad Request)} if the clothDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clothDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClothDTO> updateCloth(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClothDTO clothDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Cloth : {}, {}", id, clothDTO);
        if (clothDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clothDTO = clothService.update(clothDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothDTO.getId().toString()))
            .body(clothDTO);
    }

    /**
     * {@code PATCH  /cloths/:id} : Partial updates given fields of an existing cloth, field will ignore if it is null
     *
     * @param id the id of the clothDTO to save.
     * @param clothDTO the clothDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothDTO,
     * or with status {@code 400 (Bad Request)} if the clothDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clothDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clothDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClothDTO> partialUpdateCloth(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClothDTO clothDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Cloth partially : {}, {}", id, clothDTO);
        if (clothDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClothDTO> result = clothService.partialUpdate(clothDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cloths} : get all the cloths.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cloths in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClothDTO>> getAllCloths(
        ClothCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Cloths by criteria: {}", criteria);

        Page<ClothDTO> page = clothQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cloths/count} : count all the cloths.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCloths(ClothCriteria criteria) {
        LOG.debug("REST request to count Cloths by criteria: {}", criteria);
        return ResponseEntity.ok().body(clothQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cloths/:id} : get the "id" cloth.
     *
     * @param id the id of the clothDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clothDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClothDTO> getCloth(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Cloth : {}", id);
        Optional<ClothDTO> clothDTO = clothService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clothDTO);
    }

    /**
     * {@code DELETE  /cloths/:id} : delete the "id" cloth.
     *
     * @param id the id of the clothDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCloth(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Cloth : {}", id);
        clothService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cloths/_search?query=:query} : search for the cloth corresponding
     * to the query.
     *
     * @param query the query of the cloth search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ClothDTO>> searchCloths(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Cloths for query {}", query);
        try {
            Page<ClothDTO> page = clothService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
