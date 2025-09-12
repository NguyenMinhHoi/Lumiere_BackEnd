package com.lumi.app.web.rest;

import com.lumi.app.repository.VoucherRedemptionRepository;
import com.lumi.app.service.VoucherRedemptionService;
import com.lumi.app.service.dto.VoucherRedemptionDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.VoucherRedemption}.
 */
@RestController
@RequestMapping("/api/voucher-redemptions")
public class VoucherRedemptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherRedemptionResource.class);

    private static final String ENTITY_NAME = "voucherRedemption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VoucherRedemptionService voucherRedemptionService;

    private final VoucherRedemptionRepository voucherRedemptionRepository;

    public VoucherRedemptionResource(
        VoucherRedemptionService voucherRedemptionService,
        VoucherRedemptionRepository voucherRedemptionRepository
    ) {
        this.voucherRedemptionService = voucherRedemptionService;
        this.voucherRedemptionRepository = voucherRedemptionRepository;
    }

    /**
     * {@code POST  /voucher-redemptions} : Create a new voucherRedemption.
     *
     * @param voucherRedemptionDTO the voucherRedemptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new voucherRedemptionDTO, or with status {@code 400 (Bad Request)} if the voucherRedemption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VoucherRedemptionDTO> createVoucherRedemption(@Valid @RequestBody VoucherRedemptionDTO voucherRedemptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save VoucherRedemption : {}", voucherRedemptionDTO);
        if (voucherRedemptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new voucherRedemption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        voucherRedemptionDTO = voucherRedemptionService.save(voucherRedemptionDTO);
        return ResponseEntity.created(new URI("/api/voucher-redemptions/" + voucherRedemptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, voucherRedemptionDTO.getId().toString()))
            .body(voucherRedemptionDTO);
    }

    /**
     * {@code PUT  /voucher-redemptions/:id} : Updates an existing voucherRedemption.
     *
     * @param id the id of the voucherRedemptionDTO to save.
     * @param voucherRedemptionDTO the voucherRedemptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voucherRedemptionDTO,
     * or with status {@code 400 (Bad Request)} if the voucherRedemptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the voucherRedemptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VoucherRedemptionDTO> updateVoucherRedemption(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VoucherRedemptionDTO voucherRedemptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update VoucherRedemption : {}, {}", id, voucherRedemptionDTO);
        if (voucherRedemptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voucherRedemptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!voucherRedemptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        voucherRedemptionDTO = voucherRedemptionService.update(voucherRedemptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voucherRedemptionDTO.getId().toString()))
            .body(voucherRedemptionDTO);
    }

    /**
     * {@code PATCH  /voucher-redemptions/:id} : Partial updates given fields of an existing voucherRedemption, field will ignore if it is null
     *
     * @param id the id of the voucherRedemptionDTO to save.
     * @param voucherRedemptionDTO the voucherRedemptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voucherRedemptionDTO,
     * or with status {@code 400 (Bad Request)} if the voucherRedemptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the voucherRedemptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the voucherRedemptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VoucherRedemptionDTO> partialUpdateVoucherRedemption(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VoucherRedemptionDTO voucherRedemptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update VoucherRedemption partially : {}, {}", id, voucherRedemptionDTO);
        if (voucherRedemptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voucherRedemptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!voucherRedemptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VoucherRedemptionDTO> result = voucherRedemptionService.partialUpdate(voucherRedemptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voucherRedemptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /voucher-redemptions} : get all the voucherRedemptions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of voucherRedemptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VoucherRedemptionDTO>> getAllVoucherRedemptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of VoucherRedemptions");
        Page<VoucherRedemptionDTO> page = voucherRedemptionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /voucher-redemptions/:id} : get the "id" voucherRedemption.
     *
     * @param id the id of the voucherRedemptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the voucherRedemptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VoucherRedemptionDTO> getVoucherRedemption(@PathVariable("id") Long id) {
        LOG.debug("REST request to get VoucherRedemption : {}", id);
        Optional<VoucherRedemptionDTO> voucherRedemptionDTO = voucherRedemptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(voucherRedemptionDTO);
    }

    /**
     * {@code DELETE  /voucher-redemptions/:id} : delete the "id" voucherRedemption.
     *
     * @param id the id of the voucherRedemptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucherRedemption(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete VoucherRedemption : {}", id);
        voucherRedemptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /voucher-redemptions/_search?query=:query} : search for the voucherRedemption corresponding
     * to the query.
     *
     * @param query the query of the voucherRedemption search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<VoucherRedemptionDTO>> searchVoucherRedemptions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of VoucherRedemptions for query {}", query);
        try {
            Page<VoucherRedemptionDTO> page = voucherRedemptionService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
