package com.lumi.app.web.rest;

import com.lumi.app.repository.CompanyConfigRepository;
import com.lumi.app.service.CompanyConfigService;
import com.lumi.app.service.dto.CompanyConfigDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.CompanyConfig}.
 */
@RestController
@RequestMapping("/api/company-configs")
public class CompanyConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyConfigResource.class);

    private static final String ENTITY_NAME = "companyConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompanyConfigService companyConfigService;

    private final CompanyConfigRepository companyConfigRepository;

    public CompanyConfigResource(CompanyConfigService companyConfigService, CompanyConfigRepository companyConfigRepository) {
        this.companyConfigService = companyConfigService;
        this.companyConfigRepository = companyConfigRepository;
    }

    /**
     * {@code POST  /company-configs} : Create a new companyConfig.
     *
     * @param companyConfigDTO the companyConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new companyConfigDTO, or with status {@code 400 (Bad Request)} if the companyConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CompanyConfigDTO> createCompanyConfig(@Valid @RequestBody CompanyConfigDTO companyConfigDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CompanyConfig : {}", companyConfigDTO);
        if (companyConfigDTO.getId() != null) {
            throw new BadRequestAlertException("A new companyConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        companyConfigDTO = companyConfigService.save(companyConfigDTO);
        return ResponseEntity.created(new URI("/api/company-configs/" + companyConfigDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, companyConfigDTO.getId().toString()))
            .body(companyConfigDTO);
    }

    /**
     * {@code PUT  /company-configs/:id} : Updates an existing companyConfig.
     *
     * @param id the id of the companyConfigDTO to save.
     * @param companyConfigDTO the companyConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyConfigDTO,
     * or with status {@code 400 (Bad Request)} if the companyConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the companyConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyConfigDTO> updateCompanyConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CompanyConfigDTO companyConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CompanyConfig : {}, {}", id, companyConfigDTO);
        if (companyConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companyConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companyConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        companyConfigDTO = companyConfigService.update(companyConfigDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyConfigDTO.getId().toString()))
            .body(companyConfigDTO);
    }

    /**
     * {@code PATCH  /company-configs/:id} : Partial updates given fields of an existing companyConfig, field will ignore if it is null
     *
     * @param id the id of the companyConfigDTO to save.
     * @param companyConfigDTO the companyConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyConfigDTO,
     * or with status {@code 400 (Bad Request)} if the companyConfigDTO is not valid,
     * or with status {@code 404 (Not Found)} if the companyConfigDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the companyConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CompanyConfigDTO> partialUpdateCompanyConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CompanyConfigDTO companyConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CompanyConfig partially : {}, {}", id, companyConfigDTO);
        if (companyConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companyConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companyConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CompanyConfigDTO> result = companyConfigService.partialUpdate(companyConfigDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyConfigDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /company-configs} : get all the companyConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companyConfigs in body.
     */
    @GetMapping("")
    public List<CompanyConfigDTO> getAllCompanyConfigs() {
        LOG.debug("REST request to get all CompanyConfigs");
        return companyConfigService.findAll();
    }

    /**
     * {@code GET  /company-configs/:id} : get the "id" companyConfig.
     *
     * @param id the id of the companyConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the companyConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyConfigDTO> getCompanyConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CompanyConfig : {}", id);
        Optional<CompanyConfigDTO> companyConfigDTO = companyConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(companyConfigDTO);
    }

    /**
     * {@code DELETE  /company-configs/:id} : delete the "id" companyConfig.
     *
     * @param id the id of the companyConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompanyConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CompanyConfig : {}", id);
        companyConfigService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /company-configs/_search?query=:query} : search for the companyConfig corresponding
     * to the query.
     *
     * @param query the query of the companyConfig search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<CompanyConfigDTO> searchCompanyConfigs(@RequestParam("query") String query) {
        LOG.debug("REST request to search CompanyConfigs for query {}", query);
        try {
            return companyConfigService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
