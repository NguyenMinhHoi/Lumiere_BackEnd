package com.lumi.app.web.rest;

import com.lumi.app.repository.SurveyQuestionRepository;
import com.lumi.app.service.SurveyQuestionService;
import com.lumi.app.service.dto.SurveyQuestionDTO;
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
 * REST controller for managing {@link com.lumi.app.domain.SurveyQuestion}.
 */
@RestController
@RequestMapping("/api/survey-questions")
public class SurveyQuestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyQuestionResource.class);

    private static final String ENTITY_NAME = "surveyQuestion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SurveyQuestionService surveyQuestionService;

    private final SurveyQuestionRepository surveyQuestionRepository;

    public SurveyQuestionResource(SurveyQuestionService surveyQuestionService, SurveyQuestionRepository surveyQuestionRepository) {
        this.surveyQuestionService = surveyQuestionService;
        this.surveyQuestionRepository = surveyQuestionRepository;
    }

    /**
     * {@code POST  /survey-questions} : Create a new surveyQuestion.
     *
     * @param surveyQuestionDTO the surveyQuestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new surveyQuestionDTO, or with status {@code 400 (Bad Request)} if the surveyQuestion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SurveyQuestionDTO> createSurveyQuestion(@Valid @RequestBody SurveyQuestionDTO surveyQuestionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SurveyQuestion : {}", surveyQuestionDTO);
        if (surveyQuestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new surveyQuestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        surveyQuestionDTO = surveyQuestionService.save(surveyQuestionDTO);
        return ResponseEntity.created(new URI("/api/survey-questions/" + surveyQuestionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, surveyQuestionDTO.getId().toString()))
            .body(surveyQuestionDTO);
    }

    /**
     * {@code PUT  /survey-questions/:id} : Updates an existing surveyQuestion.
     *
     * @param id the id of the surveyQuestionDTO to save.
     * @param surveyQuestionDTO the surveyQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the surveyQuestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the surveyQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SurveyQuestionDTO> updateSurveyQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SurveyQuestionDTO surveyQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SurveyQuestion : {}, {}", id, surveyQuestionDTO);
        if (surveyQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, surveyQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        surveyQuestionDTO = surveyQuestionService.update(surveyQuestionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, surveyQuestionDTO.getId().toString()))
            .body(surveyQuestionDTO);
    }

    /**
     * {@code PATCH  /survey-questions/:id} : Partial updates given fields of an existing surveyQuestion, field will ignore if it is null
     *
     * @param id the id of the surveyQuestionDTO to save.
     * @param surveyQuestionDTO the surveyQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the surveyQuestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the surveyQuestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the surveyQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SurveyQuestionDTO> partialUpdateSurveyQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SurveyQuestionDTO surveyQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SurveyQuestion partially : {}, {}", id, surveyQuestionDTO);
        if (surveyQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, surveyQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SurveyQuestionDTO> result = surveyQuestionService.partialUpdate(surveyQuestionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, surveyQuestionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /survey-questions} : get all the surveyQuestions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of surveyQuestions in body.
     */
    @GetMapping("")
    public List<SurveyQuestionDTO> getAllSurveyQuestions() {
        LOG.debug("REST request to get all SurveyQuestions");
        return surveyQuestionService.findAll();
    }

    /**
     * {@code GET  /survey-questions/:id} : get the "id" surveyQuestion.
     *
     * @param id the id of the surveyQuestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the surveyQuestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SurveyQuestionDTO> getSurveyQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SurveyQuestion : {}", id);
        Optional<SurveyQuestionDTO> surveyQuestionDTO = surveyQuestionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(surveyQuestionDTO);
    }

    /**
     * {@code DELETE  /survey-questions/:id} : delete the "id" surveyQuestion.
     *
     * @param id the id of the surveyQuestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurveyQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SurveyQuestion : {}", id);
        surveyQuestionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /survey-questions/_search?query=:query} : search for the surveyQuestion corresponding
     * to the query.
     *
     * @param query the query of the surveyQuestion search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SurveyQuestionDTO> searchSurveyQuestions(@RequestParam("query") String query) {
        LOG.debug("REST request to search SurveyQuestions for query {}", query);
        try {
            return surveyQuestionService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
