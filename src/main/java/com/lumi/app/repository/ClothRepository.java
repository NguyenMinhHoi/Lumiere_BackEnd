package com.lumi.app.repository;

import com.lumi.app.domain.Cloth;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cloth entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClothRepository extends JpaRepository<Cloth, Long>, JpaSpecificationExecutor<Cloth> {}
