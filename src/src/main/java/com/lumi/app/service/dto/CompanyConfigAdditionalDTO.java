package com.lumi.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.CompanyConfigAdditional} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanyConfigAdditionalDTO implements Serializable {

    private Long id;

    @NotNull
    private Long companyConfigId;

    @NotNull
    private String configKey;

    private String configValue;

    private Instant createdAt;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyConfigId() {
        return companyConfigId;
    }

    public void setCompanyConfigId(Long companyConfigId) {
        this.companyConfigId = companyConfigId;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
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
        if (!(o instanceof CompanyConfigAdditionalDTO)) {
            return false;
        }

        CompanyConfigAdditionalDTO companyConfigAdditionalDTO = (CompanyConfigAdditionalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, companyConfigAdditionalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanyConfigAdditionalDTO{" +
            "id=" + getId() +
            ", companyConfigId=" + getCompanyConfigId() +
            ", configKey='" + getConfigKey() + "'" +
            ", configValue='" + getConfigValue() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
