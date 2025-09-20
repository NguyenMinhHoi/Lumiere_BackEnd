package com.lumi.app.repository;

import com.lumi.app.domain.ClothInventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClothInventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClothInventoryRepository extends JpaRepository<ClothInventory, Long> {}
