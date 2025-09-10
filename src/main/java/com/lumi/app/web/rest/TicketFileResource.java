package com.lumi.app.web.rest;

import com.lumi.app.repository.TicketFileRepository;
import com.lumi.app.service.TicketFileQueryService;
import com.lumi.app.service.TicketFileService;
import com.lumi.app.service.criteria.TicketFileCriteria;
import com.lumi.app.service.dto.TicketFileDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.TicketFile}.
 */
@RestController
@RequestMapping("/api/ticket-files")
public class TicketFileResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketFileResource.class);

    private static final String ENTITY_NAME = "ticketFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketFileService ticketFileService;

    private final TicketFileRepository ticketFileRepository;

    private final TicketFileQueryService ticketFileQueryService;

    public TicketFileResource(
        TicketFileService ticketFileService,
        TicketFileRepository ticketFileRepository,
        TicketFileQueryService ticketFileQueryService
    ) {
        this.ticketFileService = ticketFileService;
        this.ticketFileRepository = ticketFileRepository;
        this.ticketFileQueryService = ticketFileQueryService;
    }

    /**
     * {@code POST  /ticket-files} : Create a new ticketFile.
     *
     * @param ticketFileDTO the ticketFileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketFileDTO, or with status {@code 400 (Bad Request)} if the ticketFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketFileDTO> createTicketFile(@Valid @RequestBody TicketFileDTO ticketFileDTO) throws URISyntaxException {
        LOG.debug("REST request to save TicketFile : {}", ticketFileDTO);
        if (ticketFileDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketFile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketFileDTO = ticketFileService.save(ticketFileDTO);
        return ResponseEntity.created(new URI("/api/ticket-files/" + ticketFileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ticketFileDTO.getId().toString()))
            .body(ticketFileDTO);
    }

    /**
     * {@code PUT  /ticket-files/:id} : Updates an existing ticketFile.
     *
     * @param id the id of the ticketFileDTO to save.
     * @param ticketFileDTO the ticketFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketFileDTO,
     * or with status {@code 400 (Bad Request)} if the ticketFileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketFileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketFileDTO> updateTicketFile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketFileDTO ticketFileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketFile : {}, {}", id, ticketFileDTO);
        if (ticketFileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketFileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketFileDTO = ticketFileService.update(ticketFileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketFileDTO.getId().toString()))
            .body(ticketFileDTO);
    }

    /**
     * {@code PATCH  /ticket-files/:id} : Partial updates given fields of an existing ticketFile, field will ignore if it is null
     *
     * @param id the id of the ticketFileDTO to save.
     * @param ticketFileDTO the ticketFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketFileDTO,
     * or with status {@code 400 (Bad Request)} if the ticketFileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketFileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketFileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketFileDTO> partialUpdateTicketFile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketFileDTO ticketFileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketFile partially : {}, {}", id, ticketFileDTO);
        if (ticketFileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketFileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketFileDTO> result = ticketFileService.partialUpdate(ticketFileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketFileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-files} : get all the ticketFiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketFiles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TicketFileDTO>> getAllTicketFiles(
        TicketFileCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TicketFiles by criteria: {}", criteria);

        Page<TicketFileDTO> page = ticketFileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ticket-files/count} : count all the ticketFiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTicketFiles(TicketFileCriteria criteria) {
        LOG.debug("REST request to count TicketFiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(ticketFileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ticket-files/:id} : get the "id" ticketFile.
     *
     * @param id the id of the ticketFileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketFileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketFileDTO> getTicketFile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketFile : {}", id);
        Optional<TicketFileDTO> ticketFileDTO = ticketFileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketFileDTO);
    }

    /**
     * {@code DELETE  /ticket-files/:id} : delete the "id" ticketFile.
     *
     * @param id the id of the ticketFileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketFile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketFile : {}", id);
        ticketFileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /ticket-files/_search?query=:query} : search for the ticketFile corresponding
     * to the query.
     *
     * @param query the query of the ticketFile search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TicketFileDTO>> searchTicketFiles(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of TicketFiles for query {}", query);
        try {
            Page<TicketFileDTO> page = ticketFileService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
