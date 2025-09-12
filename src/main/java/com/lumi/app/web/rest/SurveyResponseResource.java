package com.lumi.app.web.rest;

import com.lumi.app.repository.SurveyResponseRepository;
import com.lumi.app.service.SurveyResponseService;
import com.lumi.app.service.dto.SurveyResponseDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.SurveyResponse}.
 */
@RestController
@RequestMapping("/api/survey-responses")
public class SurveyResponseResource {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyResponseResource.class);

    private static final String ENTITY_NAME = "surveyResponse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SurveyResponseService surveyResponseService;

    private final SurveyResponseRepository surveyResponseRepository;

    public SurveyResponseResource(SurveyResponseService surveyResponseService, SurveyResponseRepository surveyResponseRepository) {
        this.surveyResponseService = surveyResponseService;
        this.surveyResponseRepository = surveyResponseRepository;
    }

    /**
     * {@code POST  /survey-responses} : Create a new surveyResponse.
     *
     * @param surveyResponseDTO the surveyResponseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new surveyResponseDTO, or with status {@code 400 (Bad Request)} if the surveyResponse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SurveyResponseDTO> createSurveyResponse(@Valid @RequestBody SurveyResponseDTO surveyResponseDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SurveyResponse : {}", surveyResponseDTO);
        if (surveyResponseDTO.getId() != null) {
            throw new BadRequestAlertException("A new surveyResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        surveyResponseDTO = surveyResponseService.save(surveyResponseDTO);
        return ResponseEntity.created(new URI("/api/survey-responses/" + surveyResponseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, surveyResponseDTO.getId().toString()))
            .body(surveyResponseDTO);
    }

    /**
     * {@code PUT  /survey-responses/:id} : Updates an existing surveyResponse.
     *
     * @param id the id of the surveyResponseDTO to save.
     * @param surveyResponseDTO the surveyResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyResponseDTO,
     * or with status {@code 400 (Bad Request)} if the surveyResponseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the surveyResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponseDTO> updateSurveyResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SurveyResponseDTO surveyResponseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SurveyResponse : {}, {}", id, surveyResponseDTO);
        if (surveyResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, surveyResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        surveyResponseDTO = surveyResponseService.update(surveyResponseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, surveyResponseDTO.getId().toString()))
            .body(surveyResponseDTO);
    }

    /**
     * {@code PATCH  /survey-responses/:id} : Partial updates given fields of an existing surveyResponse, field will ignore if it is null
     *
     * @param id the id of the surveyResponseDTO to save.
     * @param surveyResponseDTO the surveyResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyResponseDTO,
     * or with status {@code 400 (Bad Request)} if the surveyResponseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the surveyResponseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the surveyResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SurveyResponseDTO> partialUpdateSurveyResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SurveyResponseDTO surveyResponseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SurveyResponse partially : {}, {}", id, surveyResponseDTO);
        if (surveyResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, surveyResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SurveyResponseDTO> result = surveyResponseService.partialUpdate(surveyResponseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, surveyResponseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /survey-responses} : get all the surveyResponses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of surveyResponses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SurveyResponseDTO>> getAllSurveyResponses(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of SurveyResponses");
        Page<SurveyResponseDTO> page = surveyResponseService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /survey-responses/:id} : get the "id" surveyResponse.
     *
     * @param id the id of the surveyResponseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the surveyResponseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponseDTO> getSurveyResponse(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SurveyResponse : {}", id);
        Optional<SurveyResponseDTO> surveyResponseDTO = surveyResponseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(surveyResponseDTO);
    }

    /**
     * {@code DELETE  /survey-responses/:id} : delete the "id" surveyResponse.
     *
     * @param id the id of the surveyResponseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurveyResponse(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SurveyResponse : {}", id);
        surveyResponseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /survey-responses/_search?query=:query} : search for the surveyResponse corresponding
     * to the query.
     *
     * @param query the query of the surveyResponse search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SurveyResponseDTO>> searchSurveyResponses(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of SurveyResponses for query {}", query);
        try {
            Page<SurveyResponseDTO> page = surveyResponseService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
