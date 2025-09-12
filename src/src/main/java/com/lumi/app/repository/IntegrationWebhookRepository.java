package com.lumi.app.repository;

import com.lumi.app.domain.IntegrationWebhook;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the IntegrationWebhook entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IntegrationWebhookRepository extends JpaRepository<IntegrationWebhook, Long> {}
