package com.lumi.app.web.rest;

import static com.lumi.app.domain.CampaignAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumi.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Campaign;
import com.lumi.app.domain.enumeration.DeliveryChannel;
import com.lumi.app.repository.CampaignRepository;
import com.lumi.app.repository.search.CampaignSearchRepository;
import com.lumi.app.service.dto.CampaignDTO;
import com.lumi.app.service.mapper.CampaignMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CampaignResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CampaignResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final DeliveryChannel DEFAULT_CHANNEL = DeliveryChannel.EMAIL;
    private static final DeliveryChannel UPDATED_CHANNEL = DeliveryChannel.SMS;

    private static final BigDecimal DEFAULT_BUDGET = new BigDecimal(0);
    private static final BigDecimal UPDATED_BUDGET = new BigDecimal(1);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/campaigns";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/campaigns/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignMapper campaignMapper;

    @Autowired
    private CampaignSearchRepository campaignSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCampaignMockMvc;

    private Campaign campaign;

    private Campaign insertedCampaign;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Campaign createEntity() {
        return new Campaign()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .channel(DEFAULT_CHANNEL)
            .budget(DEFAULT_BUDGET)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Campaign createUpdatedEntity() {
        return new Campaign()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .channel(UPDATED_CHANNEL)
            .budget(UPDATED_BUDGET)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        campaign = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCampaign != null) {
            campaignRepository.delete(insertedCampaign);
            campaignSearchRepository.delete(insertedCampaign);
            insertedCampaign = null;
        }
    }

    @Test
    @Transactional
    void createCampaign() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);
        var returnedCampaignDTO = om.readValue(
            restCampaignMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CampaignDTO.class
        );

        // Validate the Campaign in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCampaign = campaignMapper.toEntity(returnedCampaignDTO);
        assertCampaignUpdatableFieldsEquals(returnedCampaign, getPersistedCampaign(returnedCampaign));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCampaign = returnedCampaign;
    }

    @Test
    @Transactional
    void createCampaignWithExistingId() throws Exception {
        // Create the Campaign with an existing ID
        campaign.setId(1L);
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // set the field null
        campaign.setName(null);

        // Create the Campaign, which fails.
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // set the field null
        campaign.setChannel(null);

        // Create the Campaign, which fails.
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // set the field null
        campaign.setStartDate(null);

        // Create the Campaign, which fails.
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // set the field null
        campaign.setEndDate(null);

        // Create the Campaign, which fails.
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // set the field null
        campaign.setIsActive(null);

        // Create the Campaign, which fails.
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        // set the field null
        campaign.setCreatedAt(null);

        // Create the Campaign, which fails.
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCampaigns() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(campaign.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].budget").value(hasItem(sameNumber(DEFAULT_BUDGET))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCampaign() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);

        // Get the campaign
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL_ID, campaign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(campaign.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.budget").value(sameNumber(DEFAULT_BUDGET)))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCampaign() throws Exception {
        // Get the campaign
        restCampaignMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCampaign() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        campaignSearchRepository.save(campaign);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());

        // Update the campaign
        Campaign updatedCampaign = campaignRepository.findById(campaign.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCampaign are not directly saved in db
        em.detach(updatedCampaign);
        updatedCampaign
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .channel(UPDATED_CHANNEL)
            .budget(UPDATED_BUDGET)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CampaignDTO campaignDTO = campaignMapper.toDto(updatedCampaign);

        restCampaignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, campaignDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(campaignDTO))
            )
            .andExpect(status().isOk());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCampaignToMatchAllProperties(updatedCampaign);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Campaign> campaignSearchList = Streamable.of(campaignSearchRepository.findAll()).toList();
                Campaign testCampaignSearch = campaignSearchList.get(searchDatabaseSizeAfter - 1);

                assertCampaignAllPropertiesEquals(testCampaignSearch, updatedCampaign);
            });
    }

    @Test
    @Transactional
    void putNonExistingCampaign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        campaign.setId(longCount.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, campaignDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCampaign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        campaign.setId(longCount.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCampaign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        campaign.setId(longCount.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCampaignWithPatch() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the campaign using partial update
        Campaign partialUpdatedCampaign = new Campaign();
        partialUpdatedCampaign.setId(campaign.getId());

        partialUpdatedCampaign.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).endDate(UPDATED_END_DATE);

        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCampaign.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCampaign))
            )
            .andExpect(status().isOk());

        // Validate the Campaign in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCampaignUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCampaign, campaign), getPersistedCampaign(campaign));
    }

    @Test
    @Transactional
    void fullUpdateCampaignWithPatch() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the campaign using partial update
        Campaign partialUpdatedCampaign = new Campaign();
        partialUpdatedCampaign.setId(campaign.getId());

        partialUpdatedCampaign
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .channel(UPDATED_CHANNEL)
            .budget(UPDATED_BUDGET)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCampaign.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCampaign))
            )
            .andExpect(status().isOk());

        // Validate the Campaign in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCampaignUpdatableFieldsEquals(partialUpdatedCampaign, getPersistedCampaign(partialUpdatedCampaign));
    }

    @Test
    @Transactional
    void patchNonExistingCampaign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        campaign.setId(longCount.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, campaignDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCampaign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        campaign.setId(longCount.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCampaign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        campaign.setId(longCount.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(campaignDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Campaign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCampaign() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);
        campaignRepository.save(campaign);
        campaignSearchRepository.save(campaign);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the campaign
        restCampaignMockMvc
            .perform(delete(ENTITY_API_URL_ID, campaign.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(campaignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCampaign() throws Exception {
        // Initialize the database
        insertedCampaign = campaignRepository.saveAndFlush(campaign);
        campaignSearchRepository.save(campaign);

        // Search the campaign
        restCampaignMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + campaign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(campaign.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].budget").value(hasItem(sameNumber(DEFAULT_BUDGET))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return campaignRepository.count();
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

    protected Campaign getPersistedCampaign(Campaign campaign) {
        return campaignRepository.findById(campaign.getId()).orElseThrow();
    }

    protected void assertPersistedCampaignToMatchAllProperties(Campaign expectedCampaign) {
        assertCampaignAllPropertiesEquals(expectedCampaign, getPersistedCampaign(expectedCampaign));
    }

    protected void assertPersistedCampaignToMatchUpdatableProperties(Campaign expectedCampaign) {
        assertCampaignAllUpdatablePropertiesEquals(expectedCampaign, getPersistedCampaign(expectedCampaign));
    }
}
