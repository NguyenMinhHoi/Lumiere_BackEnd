package com.lumi.app.repository;

import com.lumi.app.domain.TicketFile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketFileRepository extends JpaRepository<TicketFile, Long>, JpaSpecificationExecutor<TicketFile> {}
