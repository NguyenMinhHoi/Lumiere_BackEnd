package com.lumi.app.web.rest;

import com.lumi.app.repository.CompanyConfigAdditionalRepository;
import com.lumi.app.service.CompanyConfigAdditionalService;
import com.lumi.app.service.dto.CompanyConfigAdditionalDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.CompanyConfigAdditional}.
 */
@RestController
@RequestMapping("/api/company-config-additionals")
public class CompanyConfigAdditionalResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyConfigAdditionalResource.class);

    private static final String ENTITY_NAME = "companyConfigAdditional";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompanyConfigAdditionalService companyConfigAdditionalService;

    private final CompanyConfigAdditionalRepository companyConfigAdditionalRepository;

    public CompanyConfigAdditionalResource(
        CompanyConfigAdditionalService companyConfigAdditionalService,
        CompanyConfigAdditionalRepository companyConfigAdditionalRepository
    ) {
        this.companyConfigAdditionalService = companyConfigAdditionalService;
        this.companyConfigAdditionalRepository = companyConfigAdditionalRepository;
    }

    /**
     * {@code POST  /company-config-additionals} : Create a new companyConfigAdditional.
     *
     * @param companyConfigAdditionalDTO the companyConfigAdditionalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new companyConfigAdditionalDTO, or with status {@code 400 (Bad Request)} if the companyConfigAdditional has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CompanyConfigAdditionalDTO> createCompanyConfigAdditional(
        @Valid @RequestBody CompanyConfigAdditionalDTO companyConfigAdditionalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CompanyConfigAdditional : {}", companyConfigAdditionalDTO);
        if (companyConfigAdditionalDTO.getId() != null) {
            throw new BadRequestAlertException("A new companyConfigAdditional cannot already have an ID", ENTITY_NAME, "idexists");
        }
        companyConfigAdditionalDTO = companyConfigAdditionalService.save(companyConfigAdditionalDTO);
        return ResponseEntity.created(new URI("/api/company-config-additionals/" + companyConfigAdditionalDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, companyConfigAdditionalDTO.getId().toString())
            )
            .body(companyConfigAdditionalDTO);
    }

    /**
     * {@code PUT  /company-config-additionals/:id} : Updates an existing companyConfigAdditional.
     *
     * @param id the id of the companyConfigAdditionalDTO to save.
     * @param companyConfigAdditionalDTO the companyConfigAdditionalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyConfigAdditionalDTO,
     * or with status {@code 400 (Bad Request)} if the companyConfigAdditionalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the companyConfigAdditionalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyConfigAdditionalDTO> updateCompanyConfigAdditional(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CompanyConfigAdditionalDTO companyConfigAdditionalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CompanyConfigAdditional : {}, {}", id, companyConfigAdditionalDTO);
        if (companyConfigAdditionalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companyConfigAdditionalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companyConfigAdditionalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        companyConfigAdditionalDTO = companyConfigAdditionalService.update(companyConfigAdditionalDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyConfigAdditionalDTO.getId().toString()))
            .body(companyConfigAdditionalDTO);
    }

    /**
     * {@code PATCH  /company-config-additionals/:id} : Partial updates given fields of an existing companyConfigAdditional, field will ignore if it is null
     *
     * @param id the id of the companyConfigAdditionalDTO to save.
     * @param companyConfigAdditionalDTO the companyConfigAdditionalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyConfigAdditionalDTO,
     * or with status {@code 400 (Bad Request)} if the companyConfigAdditionalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the companyConfigAdditionalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the companyConfigAdditionalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CompanyConfigAdditionalDTO> partialUpdateCompanyConfigAdditional(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CompanyConfigAdditionalDTO companyConfigAdditionalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CompanyConfigAdditional partially : {}, {}", id, companyConfigAdditionalDTO);
        if (companyConfigAdditionalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companyConfigAdditionalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companyConfigAdditionalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CompanyConfigAdditionalDTO> result = companyConfigAdditionalService.partialUpdate(companyConfigAdditionalDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyConfigAdditionalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /company-config-additionals} : get all the companyConfigAdditionals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companyConfigAdditionals in body.
     */
    @GetMapping("")
    public List<CompanyConfigAdditionalDTO> getAllCompanyConfigAdditionals() {
        LOG.debug("REST request to get all CompanyConfigAdditionals");
        return companyConfigAdditionalService.findAll();
    }

    /**
     * {@code GET  /company-config-additionals/:id} : get the "id" companyConfigAdditional.
     *
     * @param id the id of the companyConfigAdditionalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the companyConfigAdditionalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyConfigAdditionalDTO> getCompanyConfigAdditional(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CompanyConfigAdditional : {}", id);
        Optional<CompanyConfigAdditionalDTO> companyConfigAdditionalDTO = companyConfigAdditionalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(companyConfigAdditionalDTO);
    }

    /**
     * {@code DELETE  /company-config-additionals/:id} : delete the "id" companyConfigAdditional.
     *
     * @param id the id of the companyConfigAdditionalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompanyConfigAdditional(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CompanyConfigAdditional : {}", id);
        companyConfigAdditionalService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /company-config-additionals/_search?query=:query} : search for the companyConfigAdditional corresponding
     * to the query.
     *
     * @param query the query of the companyConfigAdditional search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<CompanyConfigAdditionalDTO> searchCompanyConfigAdditionals(@RequestParam("query") String query) {
        LOG.debug("REST request to search CompanyConfigAdditionals for query {}", query);
        try {
            return companyConfigAdditionalService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
