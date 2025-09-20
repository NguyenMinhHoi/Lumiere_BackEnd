package com.lumi.app.repository;

import com.lumi.app.domain.ClothSupplement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClothSupplement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClothSupplementRepository extends JpaRepository<ClothSupplement, Long>, JpaSpecificationExecutor<ClothSupplement> {}
