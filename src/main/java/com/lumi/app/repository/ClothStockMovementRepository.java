package com.lumi.app.repository;

import com.lumi.app.domain.ClothStockMovement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClothStockMovement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClothStockMovementRepository extends JpaRepository<ClothStockMovement, Long> {}
