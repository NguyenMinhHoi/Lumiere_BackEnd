package com.lumi.app.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.Supplement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplementDTO implements Serializable {

    private Long id;

    @NotNull
    private Long productId;

    @NotNull
    private Long supplierId;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal supplyPrice;

    @Size(max = 3)
    private String currency;

    @Min(value = 0)
    private Integer leadTimeDays;

    @Min(value = 1)
    private Integer minOrderQty;

    @NotNull
    private Boolean isPreferred;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public Integer getMinOrderQty() {
        return minOrderQty;
    }

    public void setMinOrderQty(Integer minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public Boolean getIsPreferred() {
        return isPreferred;
    }

    public void setIsPreferred(Boolean isPreferred) {
        this.isPreferred = isPreferred;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplementDTO)) {
            return false;
        }

        SupplementDTO supplementDTO = (SupplementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, supplementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplementDTO{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", supplierId=" + getSupplierId() +
            ", supplyPrice=" + getSupplyPrice() +
            ", currency='" + getCurrency() + "'" +
            ", leadTimeDays=" + getLeadTimeDays() +
            ", minOrderQty=" + getMinOrderQty() +
            ", isPreferred='" + getIsPreferred() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
