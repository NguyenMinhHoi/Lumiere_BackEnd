package com.lumi.app.web.rest;

import com.lumi.app.repository.ChannelMessageRepository;
import com.lumi.app.service.ChannelMessageService;
import com.lumi.app.service.dto.ChannelMessageDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.ChannelMessage}.
 */
@RestController
@RequestMapping("/api/channel-messages")
public class ChannelMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelMessageResource.class);

    private static final String ENTITY_NAME = "channelMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChannelMessageService channelMessageService;

    private final ChannelMessageRepository channelMessageRepository;

    public ChannelMessageResource(ChannelMessageService channelMessageService, ChannelMessageRepository channelMessageRepository) {
        this.channelMessageService = channelMessageService;
        this.channelMessageRepository = channelMessageRepository;
    }

    /**
     * {@code POST  /channel-messages} : Create a new channelMessage.
     *
     * @param channelMessageDTO the channelMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new channelMessageDTO, or with status {@code 400 (Bad Request)} if the channelMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChannelMessageDTO> createChannelMessage(@Valid @RequestBody ChannelMessageDTO channelMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ChannelMessage : {}", channelMessageDTO);
        if (channelMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new channelMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        channelMessageDTO = channelMessageService.save(channelMessageDTO);
        return ResponseEntity.created(new URI("/api/channel-messages/" + channelMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, channelMessageDTO.getId().toString()))
            .body(channelMessageDTO);
    }

    /**
     * {@code PUT  /channel-messages/:id} : Updates an existing channelMessage.
     *
     * @param id the id of the channelMessageDTO to save.
     * @param channelMessageDTO the channelMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated channelMessageDTO,
     * or with status {@code 400 (Bad Request)} if the channelMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the channelMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChannelMessageDTO> updateChannelMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChannelMessageDTO channelMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChannelMessage : {}, {}", id, channelMessageDTO);
        if (channelMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, channelMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!channelMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        channelMessageDTO = channelMessageService.update(channelMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, channelMessageDTO.getId().toString()))
            .body(channelMessageDTO);
    }

    /**
     * {@code PATCH  /channel-messages/:id} : Partial updates given fields of an existing channelMessage, field will ignore if it is null
     *
     * @param id the id of the channelMessageDTO to save.
     * @param channelMessageDTO the channelMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated channelMessageDTO,
     * or with status {@code 400 (Bad Request)} if the channelMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the channelMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the channelMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChannelMessageDTO> partialUpdateChannelMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChannelMessageDTO channelMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChannelMessage partially : {}, {}", id, channelMessageDTO);
        if (channelMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, channelMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!channelMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChannelMessageDTO> result = channelMessageService.partialUpdate(channelMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, channelMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /channel-messages} : get all the channelMessages.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of channelMessages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChannelMessageDTO>> getAllChannelMessages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ChannelMessages");
        Page<ChannelMessageDTO> page;
        if (eagerload) {
            page = channelMessageService.findAllWithEagerRelationships(pageable);
        } else {
            page = channelMessageService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /channel-messages/:id} : get the "id" channelMessage.
     *
     * @param id the id of the channelMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the channelMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChannelMessageDTO> getChannelMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChannelMessage : {}", id);
        Optional<ChannelMessageDTO> channelMessageDTO = channelMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(channelMessageDTO);
    }

    /**
     * {@code DELETE  /channel-messages/:id} : delete the "id" channelMessage.
     *
     * @param id the id of the channelMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannelMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChannelMessage : {}", id);
        channelMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /channel-messages/_search?query=:query} : search for the channelMessage corresponding
     * to the query.
     *
     * @param query the query of the channelMessage search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ChannelMessageDTO>> searchChannelMessages(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ChannelMessages for query {}", query);
        try {
            Page<ChannelMessageDTO> page = channelMessageService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
