package com.lumi.app.web.rest;

import com.lumi.app.repository.SlaPlanRepository;
import com.lumi.app.service.SlaPlanService;
import com.lumi.app.service.dto.SlaPlanDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.SlaPlan}.
 */
@RestController
@RequestMapping("/api/sla-plans")
public class SlaPlanResource {

    private static final Logger LOG = LoggerFactory.getLogger(SlaPlanResource.class);

    private static final String ENTITY_NAME = "slaPlan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SlaPlanService slaPlanService;

    private final SlaPlanRepository slaPlanRepository;

    public SlaPlanResource(SlaPlanService slaPlanService, SlaPlanRepository slaPlanRepository) {
        this.slaPlanService = slaPlanService;
        this.slaPlanRepository = slaPlanRepository;
    }

    /**
     * {@code POST  /sla-plans} : Create a new slaPlan.
     *
     * @param slaPlanDTO the slaPlanDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new slaPlanDTO, or with status {@code 400 (Bad Request)} if the slaPlan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SlaPlanDTO> createSlaPlan(@Valid @RequestBody SlaPlanDTO slaPlanDTO) throws URISyntaxException {
        LOG.debug("REST request to save SlaPlan : {}", slaPlanDTO);
        if (slaPlanDTO.getId() != null) {
            throw new BadRequestAlertException("A new slaPlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        slaPlanDTO = slaPlanService.save(slaPlanDTO);
        return ResponseEntity.created(new URI("/api/sla-plans/" + slaPlanDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, slaPlanDTO.getId().toString()))
            .body(slaPlanDTO);
    }

    /**
     * {@code PUT  /sla-plans/:id} : Updates an existing slaPlan.
     *
     * @param id the id of the slaPlanDTO to save.
     * @param slaPlanDTO the slaPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slaPlanDTO,
     * or with status {@code 400 (Bad Request)} if the slaPlanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the slaPlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SlaPlanDTO> updateSlaPlan(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SlaPlanDTO slaPlanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SlaPlan : {}, {}", id, slaPlanDTO);
        if (slaPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, slaPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!slaPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        slaPlanDTO = slaPlanService.update(slaPlanDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, slaPlanDTO.getId().toString()))
            .body(slaPlanDTO);
    }

    /**
     * {@code PATCH  /sla-plans/:id} : Partial updates given fields of an existing slaPlan, field will ignore if it is null
     *
     * @param id the id of the slaPlanDTO to save.
     * @param slaPlanDTO the slaPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slaPlanDTO,
     * or with status {@code 400 (Bad Request)} if the slaPlanDTO is not valid,
     * or with status {@code 404 (Not Found)} if the slaPlanDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the slaPlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SlaPlanDTO> partialUpdateSlaPlan(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SlaPlanDTO slaPlanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SlaPlan partially : {}, {}", id, slaPlanDTO);
        if (slaPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, slaPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!slaPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SlaPlanDTO> result = slaPlanService.partialUpdate(slaPlanDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, slaPlanDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sla-plans} : get all the slaPlans.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slaPlans in body.
     */
    @GetMapping("")
    public List<SlaPlanDTO> getAllSlaPlans() {
        LOG.debug("REST request to get all SlaPlans");
        return slaPlanService.findAll();
    }

    /**
     * {@code GET  /sla-plans/:id} : get the "id" slaPlan.
     *
     * @param id the id of the slaPlanDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the slaPlanDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SlaPlanDTO> getSlaPlan(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SlaPlan : {}", id);
        Optional<SlaPlanDTO> slaPlanDTO = slaPlanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(slaPlanDTO);
    }

    /**
     * {@code DELETE  /sla-plans/:id} : delete the "id" slaPlan.
     *
     * @param id the id of the slaPlanDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlaPlan(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SlaPlan : {}", id);
        slaPlanService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /sla-plans/_search?query=:query} : search for the slaPlan corresponding
     * to the query.
     *
     * @param query the query of the slaPlan search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SlaPlanDTO> searchSlaPlans(@RequestParam("query") String query) {
        LOG.debug("REST request to search SlaPlans for query {}", query);
        try {
            return slaPlanService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
