package com.lumi.app.repository;

import com.lumi.app.domain.TicketTag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketTag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketTagRepository extends JpaRepository<TicketTag, Long> {}
