package com.lumi.app.repository;

import com.lumi.app.domain.TicketComment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketComment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {}
