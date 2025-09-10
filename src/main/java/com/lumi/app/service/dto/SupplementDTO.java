package com.lumi.app.service.dto;

import jakarta.validation.constraints.*;
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

    private ProductDTO product;

    private SupplierDTO supplier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public SupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
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
            ", supplyPrice=" + getSupplyPrice() +
            ", currency='" + getCurrency() + "'" +
            ", leadTimeDays=" + getLeadTimeDays() +
            ", minOrderQty=" + getMinOrderQty() +
            ", isPreferred='" + getIsPreferred() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", product=" + getProduct() +
            ", supplier=" + getSupplier() +
            "}";
    }
}
