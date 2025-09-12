package com.lumi.app.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.VoucherRedemption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VoucherRedemptionDTO implements Serializable {

    private Long id;

    @NotNull
    private Long voucherId;

    @NotNull
    private Long orderId;

    @NotNull
    private Long customerId;

    @NotNull
    private Instant redeemedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VoucherRedemptionDTO)) {
            return false;
        }

        VoucherRedemptionDTO voucherRedemptionDTO = (VoucherRedemptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, voucherRedemptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VoucherRedemptionDTO{" +
            "id=" + getId() +
            ", voucherId=" + getVoucherId() +
            ", orderId=" + getOrderId() +
            ", customerId=" + getCustomerId() +
            ", redeemedAt='" + getRedeemedAt() + "'" +
            "}";
    }
}
