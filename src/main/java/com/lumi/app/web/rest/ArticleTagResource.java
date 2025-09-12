package com.lumi.app.web.rest;

import com.lumi.app.domain.ArticleTag;
import com.lumi.app.repository.ArticleTagRepository;
import com.lumi.app.repository.search.ArticleTagSearchRepository;
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
 * REST controller for managing {@link com.lumi.app.domain.ArticleTag}.
 */
@RestController
@RequestMapping("/api/article-tags")
@Transactional
public class ArticleTagResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleTagResource.class);

    private static final String ENTITY_NAME = "articleTag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArticleTagRepository articleTagRepository;

    private final ArticleTagSearchRepository articleTagSearchRepository;

    public ArticleTagResource(ArticleTagRepository articleTagRepository, ArticleTagSearchRepository articleTagSearchRepository) {
        this.articleTagRepository = articleTagRepository;
        this.articleTagSearchRepository = articleTagSearchRepository;
    }

    /**
     * {@code POST  /article-tags} : Create a new articleTag.
     *
     * @param articleTag the articleTag to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new articleTag, or with status {@code 400 (Bad Request)} if the articleTag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ArticleTag> createArticleTag(@Valid @RequestBody ArticleTag articleTag) throws URISyntaxException {
        LOG.debug("REST request to save ArticleTag : {}", articleTag);
        if (articleTag.getId() != null) {
            throw new BadRequestAlertException("A new articleTag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        articleTag = articleTagRepository.save(articleTag);
        articleTagSearchRepository.index(articleTag);
        return ResponseEntity.created(new URI("/api/article-tags/" + articleTag.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, articleTag.getId().toString()))
            .body(articleTag);
    }

    /**
     * {@code PUT  /article-tags/:id} : Updates an existing articleTag.
     *
     * @param id the id of the articleTag to save.
     * @param articleTag the articleTag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated articleTag,
     * or with status {@code 400 (Bad Request)} if the articleTag is not valid,
     * or with status {@code 500 (Internal Server Error)} if the articleTag couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArticleTag> updateArticleTag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ArticleTag articleTag
    ) throws URISyntaxException {
        LOG.debug("REST request to update ArticleTag : {}, {}", id, articleTag);
        if (articleTag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, articleTag.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!articleTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        articleTag = articleTagRepository.save(articleTag);
        articleTagSearchRepository.index(articleTag);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, articleTag.getId().toString()))
            .body(articleTag);
    }

    /**
     * {@code PATCH  /article-tags/:id} : Partial updates given fields of an existing articleTag, field will ignore if it is null
     *
     * @param id the id of the articleTag to save.
     * @param articleTag the articleTag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated articleTag,
     * or with status {@code 400 (Bad Request)} if the articleTag is not valid,
     * or with status {@code 404 (Not Found)} if the articleTag is not found,
     * or with status {@code 500 (Internal Server Error)} if the articleTag couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ArticleTag> partialUpdateArticleTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ArticleTag articleTag
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ArticleTag partially : {}, {}", id, articleTag);
        if (articleTag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, articleTag.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!articleTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ArticleTag> result = articleTagRepository
            .findById(articleTag.getId())
            .map(existingArticleTag -> {
                if (articleTag.getArticleId() != null) {
                    existingArticleTag.setArticleId(articleTag.getArticleId());
                }
                if (articleTag.getTagId() != null) {
                    existingArticleTag.setTagId(articleTag.getTagId());
                }

                return existingArticleTag;
            })
            .map(articleTagRepository::save)
            .map(savedArticleTag -> {
                articleTagSearchRepository.index(savedArticleTag);
                return savedArticleTag;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, articleTag.getId().toString())
        );
    }

    /**
     * {@code GET  /article-tags} : get all the articleTags.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of articleTags in body.
     */
    @GetMapping("")
    public List<ArticleTag> getAllArticleTags() {
        LOG.debug("REST request to get all ArticleTags");
        return articleTagRepository.findAll();
    }

    /**
     * {@code GET  /article-tags/:id} : get the "id" articleTag.
     *
     * @param id the id of the articleTag to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the articleTag, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleTag> getArticleTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ArticleTag : {}", id);
        Optional<ArticleTag> articleTag = articleTagRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(articleTag);
    }

    /**
     * {@code DELETE  /article-tags/:id} : delete the "id" articleTag.
     *
     * @param id the id of the articleTag to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ArticleTag : {}", id);
        articleTagRepository.deleteById(id);
        articleTagSearchRepository.deleteFromIndexById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /article-tags/_search?query=:query} : search for the articleTag corresponding
     * to the query.
     *
     * @param query the query of the articleTag search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ArticleTag> searchArticleTags(@RequestParam("query") String query) {
        LOG.debug("REST request to search ArticleTags for query {}", query);
        try {
            return StreamSupport.stream(articleTagSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
