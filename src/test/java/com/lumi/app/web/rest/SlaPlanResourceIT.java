package com.lumi.app.web.rest;

import static com.lumi.app.domain.SlaPlanAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.SlaPlan;
import com.lumi.app.repository.SlaPlanRepository;
import com.lumi.app.repository.search.SlaPlanSearchRepository;
import com.lumi.app.service.dto.SlaPlanDTO;
import com.lumi.app.service.mapper.SlaPlanMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SlaPlanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SlaPlanResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_FIRST_RESPONSE_MINS = 1;
    private static final Integer UPDATED_FIRST_RESPONSE_MINS = 2;

    private static final Integer DEFAULT_RESOLUTION_MINS = 5;
    private static final Integer UPDATED_RESOLUTION_MINS = 6;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/sla-plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sla-plans/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SlaPlanRepository slaPlanRepository;

    @Autowired
    private SlaPlanMapper slaPlanMapper;

    @Autowired
    private SlaPlanSearchRepository slaPlanSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSlaPlanMockMvc;

    private SlaPlan slaPlan;

    private SlaPlan insertedSlaPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SlaPlan createEntity() {
        return new SlaPlan()
            .name(DEFAULT_NAME)
            .firstResponseMins(DEFAULT_FIRST_RESPONSE_MINS)
            .resolutionMins(DEFAULT_RESOLUTION_MINS)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SlaPlan createUpdatedEntity() {
        return new SlaPlan()
            .name(UPDATED_NAME)
            .firstResponseMins(UPDATED_FIRST_RESPONSE_MINS)
            .resolutionMins(UPDATED_RESOLUTION_MINS)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        slaPlan = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSlaPlan != null) {
            slaPlanRepository.delete(insertedSlaPlan);
            slaPlanSearchRepository.delete(insertedSlaPlan);
            insertedSlaPlan = null;
        }
    }

    @Test
    @Transactional
    void createSlaPlan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);
        var returnedSlaPlanDTO = om.readValue(
            restSlaPlanMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SlaPlanDTO.class
        );

        // Validate the SlaPlan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSlaPlan = slaPlanMapper.toEntity(returnedSlaPlanDTO);
        assertSlaPlanUpdatableFieldsEquals(returnedSlaPlan, getPersistedSlaPlan(returnedSlaPlan));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSlaPlan = returnedSlaPlan;
    }

    @Test
    @Transactional
    void createSlaPlanWithExistingId() throws Exception {
        // Create the SlaPlan with an existing ID
        slaPlan.setId(1L);
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSlaPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        // set the field null
        slaPlan.setName(null);

        // Create the SlaPlan, which fails.
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        restSlaPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFirstResponseMinsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        // set the field null
        slaPlan.setFirstResponseMins(null);

        // Create the SlaPlan, which fails.
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        restSlaPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkResolutionMinsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        // set the field null
        slaPlan.setResolutionMins(null);

        // Create the SlaPlan, which fails.
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        restSlaPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        // set the field null
        slaPlan.setActive(null);

        // Create the SlaPlan, which fails.
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        restSlaPlanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSlaPlans() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);

        // Get all the slaPlanList
        restSlaPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(slaPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].firstResponseMins").value(hasItem(DEFAULT_FIRST_RESPONSE_MINS)))
            .andExpect(jsonPath("$.[*].resolutionMins").value(hasItem(DEFAULT_RESOLUTION_MINS)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSlaPlan() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);

        // Get the slaPlan
        restSlaPlanMockMvc
            .perform(get(ENTITY_API_URL_ID, slaPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(slaPlan.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.firstResponseMins").value(DEFAULT_FIRST_RESPONSE_MINS))
            .andExpect(jsonPath("$.resolutionMins").value(DEFAULT_RESOLUTION_MINS))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingSlaPlan() throws Exception {
        // Get the slaPlan
        restSlaPlanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSlaPlan() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        slaPlanSearchRepository.save(slaPlan);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());

        // Update the slaPlan
        SlaPlan updatedSlaPlan = slaPlanRepository.findById(slaPlan.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSlaPlan are not directly saved in db
        em.detach(updatedSlaPlan);
        updatedSlaPlan
            .name(UPDATED_NAME)
            .firstResponseMins(UPDATED_FIRST_RESPONSE_MINS)
            .resolutionMins(UPDATED_RESOLUTION_MINS)
            .active(UPDATED_ACTIVE);
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(updatedSlaPlan);

        restSlaPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, slaPlanDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO))
            )
            .andExpect(status().isOk());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSlaPlanToMatchAllProperties(updatedSlaPlan);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SlaPlan> slaPlanSearchList = Streamable.of(slaPlanSearchRepository.findAll()).toList();
                SlaPlan testSlaPlanSearch = slaPlanSearchList.get(searchDatabaseSizeAfter - 1);

                assertSlaPlanAllPropertiesEquals(testSlaPlanSearch, updatedSlaPlan);
            });
    }

    @Test
    @Transactional
    void putNonExistingSlaPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        slaPlan.setId(longCount.incrementAndGet());

        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSlaPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, slaPlanDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSlaPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        slaPlan.setId(longCount.incrementAndGet());

        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSlaPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(slaPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSlaPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        slaPlan.setId(longCount.incrementAndGet());

        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSlaPlanMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSlaPlanWithPatch() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the slaPlan using partial update
        SlaPlan partialUpdatedSlaPlan = new SlaPlan();
        partialUpdatedSlaPlan.setId(slaPlan.getId());

        partialUpdatedSlaPlan.name(UPDATED_NAME).firstResponseMins(UPDATED_FIRST_RESPONSE_MINS).resolutionMins(UPDATED_RESOLUTION_MINS);

        restSlaPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSlaPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSlaPlan))
            )
            .andExpect(status().isOk());

        // Validate the SlaPlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSlaPlanUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSlaPlan, slaPlan), getPersistedSlaPlan(slaPlan));
    }

    @Test
    @Transactional
    void fullUpdateSlaPlanWithPatch() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the slaPlan using partial update
        SlaPlan partialUpdatedSlaPlan = new SlaPlan();
        partialUpdatedSlaPlan.setId(slaPlan.getId());

        partialUpdatedSlaPlan
            .name(UPDATED_NAME)
            .firstResponseMins(UPDATED_FIRST_RESPONSE_MINS)
            .resolutionMins(UPDATED_RESOLUTION_MINS)
            .active(UPDATED_ACTIVE);

        restSlaPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSlaPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSlaPlan))
            )
            .andExpect(status().isOk());

        // Validate the SlaPlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSlaPlanUpdatableFieldsEquals(partialUpdatedSlaPlan, getPersistedSlaPlan(partialUpdatedSlaPlan));
    }

    @Test
    @Transactional
    void patchNonExistingSlaPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        slaPlan.setId(longCount.incrementAndGet());

        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSlaPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, slaPlanDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(slaPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSlaPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        slaPlan.setId(longCount.incrementAndGet());

        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSlaPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(slaPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSlaPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        slaPlan.setId(longCount.incrementAndGet());

        // Create the SlaPlan
        SlaPlanDTO slaPlanDTO = slaPlanMapper.toDto(slaPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSlaPlanMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(slaPlanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SlaPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSlaPlan() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);
        slaPlanRepository.save(slaPlan);
        slaPlanSearchRepository.save(slaPlan);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the slaPlan
        restSlaPlanMockMvc
            .perform(delete(ENTITY_API_URL_ID, slaPlan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(slaPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSlaPlan() throws Exception {
        // Initialize the database
        insertedSlaPlan = slaPlanRepository.saveAndFlush(slaPlan);
        slaPlanSearchRepository.save(slaPlan);

        // Search the slaPlan
        restSlaPlanMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + slaPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(slaPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].firstResponseMins").value(hasItem(DEFAULT_FIRST_RESPONSE_MINS)))
            .andExpect(jsonPath("$.[*].resolutionMins").value(hasItem(DEFAULT_RESOLUTION_MINS)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    protected long getRepositoryCount() {
        return slaPlanRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected SlaPlan getPersistedSlaPlan(SlaPlan slaPlan) {
        return slaPlanRepository.findById(slaPlan.getId()).orElseThrow();
    }

    protected void assertPersistedSlaPlanToMatchAllProperties(SlaPlan expectedSlaPlan) {
        assertSlaPlanAllPropertiesEquals(expectedSlaPlan, getPersistedSlaPlan(expectedSlaPlan));
    }

    protected void assertPersistedSlaPlanToMatchUpdatableProperties(SlaPlan expectedSlaPlan) {
        assertSlaPlanAllUpdatablePropertiesEquals(expectedSlaPlan, getPersistedSlaPlan(expectedSlaPlan));
    }
}
