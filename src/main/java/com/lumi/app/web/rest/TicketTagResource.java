package com.lumi.app.web.rest;

import com.lumi.app.domain.TicketTag;
import com.lumi.app.repository.TicketTagRepository;
import com.lumi.app.repository.search.TicketTagSearchRepository;
import com.lumi.app.web.rest.errors.BadRequestAlertException;
import com.lumi.app.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.app.domain.TicketTag}.
 */
@RestController
@RequestMapping("/api/ticket-tags")
@Transactional
public class TicketTagResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketTagResource.class);

    private static final String ENTITY_NAME = "ticketTag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketTagRepository ticketTagRepository;

    private final TicketTagSearchRepository ticketTagSearchRepository;

    public TicketTagResource(TicketTagRepository ticketTagRepository, TicketTagSearchRepository ticketTagSearchRepository) {
        this.ticketTagRepository = ticketTagRepository;
        this.ticketTagSearchRepository = ticketTagSearchRepository;
    }

    /**
     * {@code POST  /ticket-tags} : Create a new ticketTag.
     *
     * @param ticketTag the ticketTag to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketTag, or with status {@code 400 (Bad Request)} if the ticketTag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketTag> createTicketTag(@Valid @RequestBody TicketTag ticketTag) throws URISyntaxException {
        LOG.debug("REST request to save TicketTag : {}", ticketTag);
        if (ticketTag.getId() != null) {
            throw new BadRequestAlertException("A new ticketTag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketTag = ticketTagRepository.save(ticketTag);
        ticketTagSearchRepository.index(ticketTag);
        return ResponseEntity.created(new URI("/api/ticket-tags/" + ticketTag.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ticketTag.getId().toString()))
            .body(ticketTag);
    }

    /**
     * {@code PUT  /ticket-tags/:id} : Updates an existing ticketTag.
     *
     * @param id the id of the ticketTag to save.
     * @param ticketTag the ticketTag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketTag,
     * or with status {@code 400 (Bad Request)} if the ticketTag is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketTag couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketTag> updateTicketTag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketTag ticketTag
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketTag : {}, {}", id, ticketTag);
        if (ticketTag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketTag.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketTag = ticketTagRepository.save(ticketTag);
        ticketTagSearchRepository.index(ticketTag);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketTag.getId().toString()))
            .body(ticketTag);
    }

    /**
     * {@code PATCH  /ticket-tags/:id} : Partial updates given fields of an existing ticketTag, field will ignore if it is null
     *
     * @param id the id of the ticketTag to save.
     * @param ticketTag the ticketTag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketTag,
     * or with status {@code 400 (Bad Request)} if the ticketTag is not valid,
     * or with status {@code 404 (Not Found)} if the ticketTag is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketTag couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketTag> partialUpdateTicketTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketTag ticketTag
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketTag partially : {}, {}", id, ticketTag);
        if (ticketTag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketTag.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketTag> result = ticketTagRepository
            .findById(ticketTag.getId())
            .map(existingTicketTag -> {
                if (ticketTag.getTicketId() != null) {
                    existingTicketTag.setTicketId(ticketTag.getTicketId());
                }
                if (ticketTag.getTagId() != null) {
                    existingTicketTag.setTagId(ticketTag.getTagId());
                }

                return existingTicketTag;
            })
            .map(ticketTagRepository::save)
            .map(savedTicketTag -> {
                ticketTagSearchRepository.index(savedTicketTag);
                return savedTicketTag;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketTag.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-tags} : get all the ticketTags.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketTags in body.
     */
    @GetMapping("")
    public List<TicketTag> getAllTicketTags() {
        LOG.debug("REST request to get all TicketTags");
        return ticketTagRepository.findAll();
    }

    /**
     * {@code GET  /ticket-tags/:id} : get the "id" ticketTag.
     *
     * @param id the id of the ticketTag to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketTag, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketTag> getTicketTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketTag : {}", id);
        Optional<TicketTag> ticketTag = ticketTagRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ticketTag);
    }

    /**
     * {@code DELETE  /ticket-tags/:id} : delete the "id" ticketTag.
     *
     * @param id the id of the ticketTag to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketTag : {}", id);
        ticketTagRepository.deleteById(id);
        ticketTagSearchRepository.deleteFromIndexById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /ticket-tags/_search?query=:query} : search for the ticketTag corresponding
     * to the query.
     *
     * @param query the query of the ticketTag search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<TicketTag> searchTicketTags(@RequestParam("query") String query) {
        LOG.debug("REST request to search TicketTags for query {}", query);
        try {
            return StreamSupport.stream(ticketTagSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
