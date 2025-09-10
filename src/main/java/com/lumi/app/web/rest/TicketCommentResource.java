package com.lumi.app.web.rest;

import com.lumi.app.repository.TicketCommentRepository;
import com.lumi.app.service.TicketCommentService;
import com.lumi.app.service.dto.TicketCommentDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.TicketComment}.
 */
@RestController
@RequestMapping("/api/ticket-comments")
public class TicketCommentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCommentResource.class);

    private static final String ENTITY_NAME = "ticketComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketCommentService ticketCommentService;

    private final TicketCommentRepository ticketCommentRepository;

    public TicketCommentResource(TicketCommentService ticketCommentService, TicketCommentRepository ticketCommentRepository) {
        this.ticketCommentService = ticketCommentService;
        this.ticketCommentRepository = ticketCommentRepository;
    }

    /**
     * {@code POST  /ticket-comments} : Create a new ticketComment.
     *
     * @param ticketCommentDTO the ticketCommentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketCommentDTO, or with status {@code 400 (Bad Request)} if the ticketComment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketCommentDTO> createTicketComment(@Valid @RequestBody TicketCommentDTO ticketCommentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TicketComment : {}", ticketCommentDTO);
        if (ticketCommentDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketComment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketCommentDTO = ticketCommentService.save(ticketCommentDTO);
        return ResponseEntity.created(new URI("/api/ticket-comments/" + ticketCommentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ticketCommentDTO.getId().toString()))
            .body(ticketCommentDTO);
    }

    /**
     * {@code PUT  /ticket-comments/:id} : Updates an existing ticketComment.
     *
     * @param id the id of the ticketCommentDTO to save.
     * @param ticketCommentDTO the ticketCommentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketCommentDTO,
     * or with status {@code 400 (Bad Request)} if the ticketCommentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketCommentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketCommentDTO> updateTicketComment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketCommentDTO ticketCommentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketComment : {}, {}", id, ticketCommentDTO);
        if (ticketCommentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketCommentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketCommentDTO = ticketCommentService.update(ticketCommentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketCommentDTO.getId().toString()))
            .body(ticketCommentDTO);
    }

    /**
     * {@code PATCH  /ticket-comments/:id} : Partial updates given fields of an existing ticketComment, field will ignore if it is null
     *
     * @param id the id of the ticketCommentDTO to save.
     * @param ticketCommentDTO the ticketCommentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketCommentDTO,
     * or with status {@code 400 (Bad Request)} if the ticketCommentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketCommentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketCommentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketCommentDTO> partialUpdateTicketComment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketCommentDTO ticketCommentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketComment partially : {}, {}", id, ticketCommentDTO);
        if (ticketCommentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketCommentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketCommentDTO> result = ticketCommentService.partialUpdate(ticketCommentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketCommentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-comments} : get all the ticketComments.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketComments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TicketCommentDTO>> getAllTicketComments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of TicketComments");
        Page<TicketCommentDTO> page;
        if (eagerload) {
            page = ticketCommentService.findAllWithEagerRelationships(pageable);
        } else {
            page = ticketCommentService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ticket-comments/:id} : get the "id" ticketComment.
     *
     * @param id the id of the ticketCommentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketCommentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketCommentDTO> getTicketComment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketComment : {}", id);
        Optional<TicketCommentDTO> ticketCommentDTO = ticketCommentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketCommentDTO);
    }

    /**
     * {@code DELETE  /ticket-comments/:id} : delete the "id" ticketComment.
     *
     * @param id the id of the ticketCommentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketComment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketComment : {}", id);
        ticketCommentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /ticket-comments/_search?query=:query} : search for the ticketComment corresponding
     * to the query.
     *
     * @param query the query of the ticketComment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TicketCommentDTO>> searchTicketComments(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of TicketComments for query {}", query);
        try {
            Page<TicketCommentDTO> page = ticketCommentService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
