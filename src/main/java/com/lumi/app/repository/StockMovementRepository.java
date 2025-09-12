package com.lumi.app.repository;

import com.lumi.app.domain.StockMovement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockMovement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {}
