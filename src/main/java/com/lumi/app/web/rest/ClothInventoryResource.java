package com.lumi.app.web.rest;

import com.lumi.app.repository.ClothInventoryRepository;
import com.lumi.app.service.ClothInventoryService;
import com.lumi.app.service.dto.ClothInventoryDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.ClothInventory}.
 */
@RestController
@RequestMapping("/api/cloth-inventories")
public class ClothInventoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClothInventoryResource.class);

    private static final String ENTITY_NAME = "clothInventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClothInventoryService clothInventoryService;

    private final ClothInventoryRepository clothInventoryRepository;

    public ClothInventoryResource(ClothInventoryService clothInventoryService, ClothInventoryRepository clothInventoryRepository) {
        this.clothInventoryService = clothInventoryService;
        this.clothInventoryRepository = clothInventoryRepository;
    }

    /**
     * {@code POST  /cloth-inventories} : Create a new clothInventory.
     *
     * @param clothInventoryDTO the clothInventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clothInventoryDTO, or with status {@code 400 (Bad Request)} if the clothInventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClothInventoryDTO> createClothInventory(@Valid @RequestBody ClothInventoryDTO clothInventoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClothInventory : {}", clothInventoryDTO);
        if (clothInventoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new clothInventory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clothInventoryDTO = clothInventoryService.save(clothInventoryDTO);
        return ResponseEntity.created(new URI("/api/cloth-inventories/" + clothInventoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clothInventoryDTO.getId().toString()))
            .body(clothInventoryDTO);
    }

    /**
     * {@code PUT  /cloth-inventories/:id} : Updates an existing clothInventory.
     *
     * @param id the id of the clothInventoryDTO to save.
     * @param clothInventoryDTO the clothInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothInventoryDTO,
     * or with status {@code 400 (Bad Request)} if the clothInventoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clothInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClothInventoryDTO> updateClothInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClothInventoryDTO clothInventoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClothInventory : {}, {}", id, clothInventoryDTO);
        if (clothInventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothInventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clothInventoryDTO = clothInventoryService.update(clothInventoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothInventoryDTO.getId().toString()))
            .body(clothInventoryDTO);
    }

    /**
     * {@code PATCH  /cloth-inventories/:id} : Partial updates given fields of an existing clothInventory, field will ignore if it is null
     *
     * @param id the id of the clothInventoryDTO to save.
     * @param clothInventoryDTO the clothInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clothInventoryDTO,
     * or with status {@code 400 (Bad Request)} if the clothInventoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clothInventoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clothInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClothInventoryDTO> partialUpdateClothInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClothInventoryDTO clothInventoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClothInventory partially : {}, {}", id, clothInventoryDTO);
        if (clothInventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clothInventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clothInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClothInventoryDTO> result = clothInventoryService.partialUpdate(clothInventoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clothInventoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cloth-inventories} : get all the clothInventories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clothInventories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClothInventoryDTO>> getAllClothInventories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ClothInventories");
        Page<ClothInventoryDTO> page = clothInventoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cloth-inventories/:id} : get the "id" clothInventory.
     *
     * @param id the id of the clothInventoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clothInventoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClothInventoryDTO> getClothInventory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClothInventory : {}", id);
        Optional<ClothInventoryDTO> clothInventoryDTO = clothInventoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clothInventoryDTO);
    }

    /**
     * {@code DELETE  /cloth-inventories/:id} : delete the "id" clothInventory.
     *
     * @param id the id of the clothInventoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothInventory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClothInventory : {}", id);
        clothInventoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cloth-inventories/_search?query=:query} : search for the clothInventory corresponding
     * to the query.
     *
     * @param query the query of the clothInventory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ClothInventoryDTO>> searchClothInventories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ClothInventories for query {}", query);
        try {
            Page<ClothInventoryDTO> page = clothInventoryService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
