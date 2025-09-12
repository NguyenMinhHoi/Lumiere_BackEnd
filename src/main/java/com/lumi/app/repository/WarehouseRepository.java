package com.lumi.app.repository;

import com.lumi.app.domain.Warehouse;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Warehouse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {}
