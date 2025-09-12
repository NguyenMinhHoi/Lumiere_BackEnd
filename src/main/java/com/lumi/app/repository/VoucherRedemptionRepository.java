package com.lumi.app.repository;

import com.lumi.app.domain.VoucherRedemption;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VoucherRedemption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VoucherRedemptionRepository extends JpaRepository<VoucherRedemption, Long> {}
