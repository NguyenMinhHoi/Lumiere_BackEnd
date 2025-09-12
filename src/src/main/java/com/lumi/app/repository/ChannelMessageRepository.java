package com.lumi.app.repository;

import com.lumi.app.domain.ChannelMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChannelMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {}
