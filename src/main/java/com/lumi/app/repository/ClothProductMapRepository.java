package com.lumi.app.repository;

import com.lumi.app.domain.ClothProductMap;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClothProductMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClothProductMapRepository extends JpaRepository<ClothProductMap, Long>, JpaSpecificationExecutor<ClothProductMap> {}
