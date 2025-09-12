package com.lumi.app.web.rest;

import static com.lumi.app.domain.NotificationAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.Notification;
import com.lumi.app.domain.enumeration.DeliveryChannel;
import com.lumi.app.domain.enumeration.NotificationType;
import com.lumi.app.domain.enumeration.SendStatus;
import com.lumi.app.repository.NotificationRepository;
import com.lumi.app.repository.search.NotificationSearchRepository;
import com.lumi.app.service.dto.NotificationDTO;
import com.lumi.app.service.mapper.NotificationMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    private static final Long DEFAULT_TICKET_ID = 1L;
    private static final Long UPDATED_TICKET_ID = 2L;
    private static final Long SMALLER_TICKET_ID = 1L - 1L;

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;
    private static final Long SMALLER_CUSTOMER_ID = 1L - 1L;

    private static final Long DEFAULT_SURVEY_ID = 1L;
    private static final Long UPDATED_SURVEY_ID = 2L;
    private static final Long SMALLER_SURVEY_ID = 1L - 1L;

    private static final NotificationType DEFAULT_TYPE = NotificationType.TICKET_UPDATE;
    private static final NotificationType UPDATED_TYPE = NotificationType.SURVEY;

    private static final DeliveryChannel DEFAULT_CHANNEL = DeliveryChannel.EMAIL;
    private static final DeliveryChannel UPDATED_CHANNEL = DeliveryChannel.SMS;

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final SendStatus DEFAULT_SEND_STATUS = SendStatus.PENDING;
    private static final SendStatus UPDATED_SEND_STATUS = SendStatus.SENT;

    private static final Integer DEFAULT_RETRY_COUNT = 0;
    private static final Integer UPDATED_RETRY_COUNT = 1;
    private static final Integer SMALLER_RETRY_COUNT = 0 - 1;

    private static final Instant DEFAULT_LAST_TRIED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_TRIED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/notifications/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationSearchRepository notificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    private Notification notification;

    private Notification insertedNotification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity() {
        return new Notification()
            .ticketId(DEFAULT_TICKET_ID)
            .customerId(DEFAULT_CUSTOMER_ID)
            .surveyId(DEFAULT_SURVEY_ID)
            .type(DEFAULT_TYPE)
            .channel(DEFAULT_CHANNEL)
            .subject(DEFAULT_SUBJECT)
            .payload(DEFAULT_PAYLOAD)
            .sendStatus(DEFAULT_SEND_STATUS)
            .retryCount(DEFAULT_RETRY_COUNT)
            .lastTriedAt(DEFAULT_LAST_TRIED_AT)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity() {
        return new Notification()
            .ticketId(UPDATED_TICKET_ID)
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyId(UPDATED_SURVEY_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .subject(UPDATED_SUBJECT)
            .payload(UPDATED_PAYLOAD)
            .sendStatus(UPDATED_SEND_STATUS)
            .retryCount(UPDATED_RETRY_COUNT)
            .lastTriedAt(UPDATED_LAST_TRIED_AT)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        notification = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotification != null) {
            notificationRepository.delete(insertedNotification);
            notificationSearchRepository.delete(insertedNotification);
            insertedNotification = null;
        }
    }

    @Test
    @Transactional
    void createNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        var returnedNotificationDTO = om.readValue(
            restNotificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationDTO.class
        );

        // Validate the Notification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotification = notificationMapper.toEntity(returnedNotificationDTO);
        assertNotificationUpdatableFieldsEquals(returnedNotification, getPersistedNotification(returnedNotification));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedNotification = returnedNotification;
    }

    @Test
    @Transactional
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setType(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setChannel(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSendStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setSendStatus(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRetryCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setRetryCount(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setCreatedAt(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllNotifications() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].sendStatus").value(hasItem(DEFAULT_SEND_STATUS.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].lastTriedAt").value(hasItem(DEFAULT_LAST_TRIED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.ticketId").value(DEFAULT_TICKET_ID.intValue()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID.intValue()))
            .andExpect(jsonPath("$.surveyId").value(DEFAULT_SURVEY_ID.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.sendStatus").value(DEFAULT_SEND_STATUS.toString()))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT))
            .andExpect(jsonPath("$.lastTriedAt").value(DEFAULT_LAST_TRIED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultNotificationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultNotificationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId equals to
        defaultNotificationFiltering("ticketId.equals=" + DEFAULT_TICKET_ID, "ticketId.equals=" + UPDATED_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId in
        defaultNotificationFiltering("ticketId.in=" + DEFAULT_TICKET_ID + "," + UPDATED_TICKET_ID, "ticketId.in=" + UPDATED_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId is not null
        defaultNotificationFiltering("ticketId.specified=true", "ticketId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId is greater than or equal to
        defaultNotificationFiltering(
            "ticketId.greaterThanOrEqual=" + DEFAULT_TICKET_ID,
            "ticketId.greaterThanOrEqual=" + UPDATED_TICKET_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId is less than or equal to
        defaultNotificationFiltering("ticketId.lessThanOrEqual=" + DEFAULT_TICKET_ID, "ticketId.lessThanOrEqual=" + SMALLER_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId is less than
        defaultNotificationFiltering("ticketId.lessThan=" + UPDATED_TICKET_ID, "ticketId.lessThan=" + DEFAULT_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByTicketIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where ticketId is greater than
        defaultNotificationFiltering("ticketId.greaterThan=" + SMALLER_TICKET_ID, "ticketId.greaterThan=" + DEFAULT_TICKET_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId equals to
        defaultNotificationFiltering("customerId.equals=" + DEFAULT_CUSTOMER_ID, "customerId.equals=" + UPDATED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId in
        defaultNotificationFiltering(
            "customerId.in=" + DEFAULT_CUSTOMER_ID + "," + UPDATED_CUSTOMER_ID,
            "customerId.in=" + UPDATED_CUSTOMER_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId is not null
        defaultNotificationFiltering("customerId.specified=true", "customerId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId is greater than or equal to
        defaultNotificationFiltering(
            "customerId.greaterThanOrEqual=" + DEFAULT_CUSTOMER_ID,
            "customerId.greaterThanOrEqual=" + UPDATED_CUSTOMER_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId is less than or equal to
        defaultNotificationFiltering(
            "customerId.lessThanOrEqual=" + DEFAULT_CUSTOMER_ID,
            "customerId.lessThanOrEqual=" + SMALLER_CUSTOMER_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId is less than
        defaultNotificationFiltering("customerId.lessThan=" + UPDATED_CUSTOMER_ID, "customerId.lessThan=" + DEFAULT_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByCustomerIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where customerId is greater than
        defaultNotificationFiltering("customerId.greaterThan=" + SMALLER_CUSTOMER_ID, "customerId.greaterThan=" + DEFAULT_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId equals to
        defaultNotificationFiltering("surveyId.equals=" + DEFAULT_SURVEY_ID, "surveyId.equals=" + UPDATED_SURVEY_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId in
        defaultNotificationFiltering("surveyId.in=" + DEFAULT_SURVEY_ID + "," + UPDATED_SURVEY_ID, "surveyId.in=" + UPDATED_SURVEY_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId is not null
        defaultNotificationFiltering("surveyId.specified=true", "surveyId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId is greater than or equal to
        defaultNotificationFiltering(
            "surveyId.greaterThanOrEqual=" + DEFAULT_SURVEY_ID,
            "surveyId.greaterThanOrEqual=" + UPDATED_SURVEY_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId is less than or equal to
        defaultNotificationFiltering("surveyId.lessThanOrEqual=" + DEFAULT_SURVEY_ID, "surveyId.lessThanOrEqual=" + SMALLER_SURVEY_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId is less than
        defaultNotificationFiltering("surveyId.lessThan=" + UPDATED_SURVEY_ID, "surveyId.lessThan=" + DEFAULT_SURVEY_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsBySurveyIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where surveyId is greater than
        defaultNotificationFiltering("surveyId.greaterThan=" + SMALLER_SURVEY_ID, "surveyId.greaterThan=" + DEFAULT_SURVEY_ID);
    }

    @Test
    @Transactional
    void getAllNotificationsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where type equals to
        defaultNotificationFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where type in
        defaultNotificationFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where type is not null
        defaultNotificationFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByChannelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where channel equals to
        defaultNotificationFiltering("channel.equals=" + DEFAULT_CHANNEL, "channel.equals=" + UPDATED_CHANNEL);
    }

    @Test
    @Transactional
    void getAllNotificationsByChannelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where channel in
        defaultNotificationFiltering("channel.in=" + DEFAULT_CHANNEL + "," + UPDATED_CHANNEL, "channel.in=" + UPDATED_CHANNEL);
    }

    @Test
    @Transactional
    void getAllNotificationsByChannelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where channel is not null
        defaultNotificationFiltering("channel.specified=true", "channel.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject equals to
        defaultNotificationFiltering("subject.equals=" + DEFAULT_SUBJECT, "subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllNotificationsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject in
        defaultNotificationFiltering("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT, "subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllNotificationsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject is not null
        defaultNotificationFiltering("subject.specified=true", "subject.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject contains
        defaultNotificationFiltering("subject.contains=" + DEFAULT_SUBJECT, "subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllNotificationsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject does not contain
        defaultNotificationFiltering("subject.doesNotContain=" + UPDATED_SUBJECT, "subject.doesNotContain=" + DEFAULT_SUBJECT);
    }

    @Test
    @Transactional
    void getAllNotificationsBySendStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sendStatus equals to
        defaultNotificationFiltering("sendStatus.equals=" + DEFAULT_SEND_STATUS, "sendStatus.equals=" + UPDATED_SEND_STATUS);
    }

    @Test
    @Transactional
    void getAllNotificationsBySendStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sendStatus in
        defaultNotificationFiltering(
            "sendStatus.in=" + DEFAULT_SEND_STATUS + "," + UPDATED_SEND_STATUS,
            "sendStatus.in=" + UPDATED_SEND_STATUS
        );
    }

    @Test
    @Transactional
    void getAllNotificationsBySendStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sendStatus is not null
        defaultNotificationFiltering("sendStatus.specified=true", "sendStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount equals to
        defaultNotificationFiltering("retryCount.equals=" + DEFAULT_RETRY_COUNT, "retryCount.equals=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount in
        defaultNotificationFiltering(
            "retryCount.in=" + DEFAULT_RETRY_COUNT + "," + UPDATED_RETRY_COUNT,
            "retryCount.in=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount is not null
        defaultNotificationFiltering("retryCount.specified=true", "retryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount is greater than or equal to
        defaultNotificationFiltering(
            "retryCount.greaterThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.greaterThanOrEqual=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount is less than or equal to
        defaultNotificationFiltering(
            "retryCount.lessThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.lessThanOrEqual=" + SMALLER_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount is less than
        defaultNotificationFiltering("retryCount.lessThan=" + UPDATED_RETRY_COUNT, "retryCount.lessThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllNotificationsByRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where retryCount is greater than
        defaultNotificationFiltering("retryCount.greaterThan=" + SMALLER_RETRY_COUNT, "retryCount.greaterThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllNotificationsByLastTriedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where lastTriedAt equals to
        defaultNotificationFiltering("lastTriedAt.equals=" + DEFAULT_LAST_TRIED_AT, "lastTriedAt.equals=" + UPDATED_LAST_TRIED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByLastTriedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where lastTriedAt in
        defaultNotificationFiltering(
            "lastTriedAt.in=" + DEFAULT_LAST_TRIED_AT + "," + UPDATED_LAST_TRIED_AT,
            "lastTriedAt.in=" + UPDATED_LAST_TRIED_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationsByLastTriedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where lastTriedAt is not null
        defaultNotificationFiltering("lastTriedAt.specified=true", "lastTriedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt equals to
        defaultNotificationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt in
        defaultNotificationFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt is not null
        defaultNotificationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultNotificationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultNotificationShouldBeFound(shouldBeFound);
        defaultNotificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].sendStatus").value(hasItem(DEFAULT_SEND_STATUS.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].lastTriedAt").value(hasItem(DEFAULT_LAST_TRIED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationSearchRepository.save(notification);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .ticketId(UPDATED_TICKET_ID)
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyId(UPDATED_SURVEY_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .subject(UPDATED_SUBJECT)
            .payload(UPDATED_PAYLOAD)
            .sendStatus(UPDATED_SEND_STATUS)
            .retryCount(UPDATED_RETRY_COUNT)
            .lastTriedAt(UPDATED_LAST_TRIED_AT)
            .createdAt(UPDATED_CREATED_AT);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationToMatchAllProperties(updatedNotification);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Notification> notificationSearchList = Streamable.of(notificationSearchRepository.findAll()).toList();
                Notification testNotificationSearch = notificationSearchList.get(searchDatabaseSizeAfter - 1);

                assertNotificationAllPropertiesEquals(testNotificationSearch, updatedNotification);
            });
    }

    @Test
    @Transactional
    void putNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyId(UPDATED_SURVEY_ID)
            .subject(UPDATED_SUBJECT)
            .sendStatus(UPDATED_SEND_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotification, notification),
            getPersistedNotification(notification)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .ticketId(UPDATED_TICKET_ID)
            .customerId(UPDATED_CUSTOMER_ID)
            .surveyId(UPDATED_SURVEY_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .subject(UPDATED_SUBJECT)
            .payload(UPDATED_PAYLOAD)
            .sendStatus(UPDATED_SEND_STATUS)
            .retryCount(UPDATED_RETRY_COUNT)
            .lastTriedAt(UPDATED_LAST_TRIED_AT)
            .createdAt(UPDATED_CREATED_AT);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(partialUpdatedNotification, getPersistedNotification(partialUpdatedNotification));
    }

    @Test
    @Transactional
    void patchNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);
        notificationRepository.save(notification);
        notificationSearchRepository.save(notification);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.saveAndFlush(notification);
        notificationSearchRepository.save(notification);

        // Search the notification
        restNotificationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].ticketId").value(hasItem(DEFAULT_TICKET_ID.intValue())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD.toString())))
            .andExpect(jsonPath("$.[*].sendStatus").value(hasItem(DEFAULT_SEND_STATUS.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].lastTriedAt").value(hasItem(DEFAULT_LAST_TRIED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return notificationRepository.count();
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

    protected Notification getPersistedNotification(Notification notification) {
        return notificationRepository.findById(notification.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationToMatchAllProperties(Notification expectedNotification) {
        assertNotificationAllPropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }

    protected void assertPersistedNotificationToMatchUpdatableProperties(Notification expectedNotification) {
        assertNotificationAllUpdatablePropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }
}
