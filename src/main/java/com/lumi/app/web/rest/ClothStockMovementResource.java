package com.lumi.app.web.rest;

import com.lumi.app.repository.ClothStockMovementRepository;
import com.lumi.app.service.ClothStockMovementService;
import com.lumi.app.service.dto.ClothStockMovementDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.ClothStockMovement}.
 */
@RestController
@RequestMapping("/api/cloth-stock-movements")
public class ClothStockMovementResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClothStockMovementResource.class);

    private static final String ENTITY_NAME = "clothStockMovement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClothStockMovementService clothStockMovementService;

    private final ClothStockMovementRepository clothStockMovementRepository;

    public ClothStockMovementResource(
        ClothStockMovementService clothStockMovementService,
        ClothStockMovementRepository clothStockMovementRepository
    ) {
        this.clothStockMovementService = clothStockMovementService;
        this.clothStockMovementRepository = clothStockMovementRepository;
    }

    /**
     * {@code POST  /cloth-stock-movements} : Create a new clothStockMovement.
     *
     * @param clothStockMovementDTO the clothStockMovementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clothStockMovementDTO, or with status {@code 400 (Bad Request)} if the clothStockMovement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClothStockMovementDTO> createClothStockMovement(@Valid @RequestBody ClothStockMovementDTO clothStockMovementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClothStockMovement : {}", clothStockMovementDTO);
        if (clothStockMovementDTO.getId() != null) {
            throw new BadRequestAlertException("A new clothStockMovement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clothStockMovementDTO = clothStockMovementService.save(clothStockMovementDTO);
        return ResponseEntity.created(new URI("/api/cloth-stock-movements/" + clothStockMovementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clothStockMovementDTO.getId().toString()))
            .body(clothStockMovementDTO);
    }

    /**
     * {@code PUT  /cloth-stock-movements/:id} : Updates an existing clothStockMovement.
     *
     * @param id the id of the clothStockMovementDTO to save.
     * @param clothStockMovementDTO the clothStockMovementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothStockMovementDTO,
     * or with status {@code 400 (Bad Request)} if the clothStockMovementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clothStockMovementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClothStockMovementDTO> updateClothStockMovement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClothStockMovementDTO clothStockMovementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClothStockMovement : {}, {}", id, clothStockMovementDTO);
        if (clothStockMovementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothStockMovementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothStockMovementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clothStockMovementDTO = clothStockMovementService.update(clothStockMovementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothStockMovementDTO.getId().toString()))
            .body(clothStockMovementDTO);
    }

    /**
     * {@code PATCH  /cloth-stock-movements/:id} : Partial updates given fields of an existing clothStockMovement, field will ignore if it is null
     *
     * @param id the id of the clothStockMovementDTO to save.
     * @param clothStockMovementDTO the clothStockMovementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothStockMovementDTO,
     * or with status {@code 400 (Bad Request)} if the clothStockMovementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clothStockMovementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clothStockMovementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClothStockMovementDTO> partialUpdateClothStockMovement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClothStockMovementDTO clothStockMovementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClothStockMovement partially : {}, {}", id, clothStockMovementDTO);
        if (clothStockMovementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothStockMovementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothStockMovementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClothStockMovementDTO> result = clothStockMovementService.partialUpdate(clothStockMovementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothStockMovementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cloth-stock-movements} : get all the clothStockMovements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clothStockMovements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClothStockMovementDTO>> getAllClothStockMovements(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ClothStockMovements");
        Page<ClothStockMovementDTO> page = clothStockMovementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cloth-stock-movements/:id} : get the "id" clothStockMovement.
     *
     * @param id the id of the clothStockMovementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clothStockMovementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClothStockMovementDTO> getClothStockMovement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClothStockMovement : {}", id);
        Optional<ClothStockMovementDTO> clothStockMovementDTO = clothStockMovementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clothStockMovementDTO);
    }

    /**
     * {@code DELETE  /cloth-stock-movements/:id} : delete the "id" clothStockMovement.
     *
     * @param id the id of the clothStockMovementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothStockMovement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClothStockMovement : {}", id);
        clothStockMovementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cloth-stock-movements/_search?query=:query} : search for the clothStockMovement corresponding
     * to the query.
     *
     * @param query the query of the clothStockMovement search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ClothStockMovementDTO>> searchClothStockMovements(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ClothStockMovements for query {}", query);
        try {
            Page<ClothStockMovementDTO> page = clothStockMovementService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
