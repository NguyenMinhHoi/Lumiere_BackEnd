package com.lumi.app.service;

import com.lumi.app.domain.*; // for static metamodels
import com.lumi.app.domain.Notification;
import com.lumi.app.repository.NotificationRepository;
import com.lumi.app.repository.search.NotificationSearchRepository;
import com.lumi.app.service.criteria.NotificationCriteria;
import com.lumi.app.service.dto.NotificationDTO;
import com.lumi.app.service.mapper.NotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Notification} entities in the database.
 * The main input is a {@link NotificationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link NotificationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationQueryService extends QueryService<Notification> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationQueryService.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSearchRepository notificationSearchRepository;

    public NotificationQueryService(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        NotificationSearchRepository notificationSearchRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationSearchRepository = notificationSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link NotificationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findByCriteria(NotificationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Notification> specification = createSpecification(criteria);
        return notificationRepository.findAll(specification, page).map(notificationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Notification> specification = createSpecification(criteria);
        return notificationRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Notification> createSpecification(NotificationCriteria criteria) {
        Specification<Notification> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Notification_.id),
                buildRangeSpecification(criteria.getTicketId(), Notification_.ticketId),
                buildRangeSpecification(criteria.getCustomerId(), Notification_.customerId),
                buildRangeSpecification(criteria.getSurveyId(), Notification_.surveyId),
                buildSpecification(criteria.getType(), Notification_.type),
                buildSpecification(criteria.getChannel(), Notification_.channel),
                buildStringSpecification(criteria.getSubject(), Notification_.subject),
                buildSpecification(criteria.getSendStatus(), Notification_.sendStatus),
                buildRangeSpecification(criteria.getRetryCount(), Notification_.retryCount),
                buildRangeSpecification(criteria.getLastTriedAt(), Notification_.lastTriedAt),
                buildRangeSpecification(criteria.getCreatedAt(), Notification_.createdAt)
            );
        }
        return specification;
    }
}
