package com.lumi.app.repository;

import com.lumi.app.domain.ProductVariant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductVariant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>, JpaSpecificationExecutor<ProductVariant> {}
