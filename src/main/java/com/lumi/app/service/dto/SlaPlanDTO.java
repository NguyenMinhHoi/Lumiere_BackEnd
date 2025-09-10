package com.lumi.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.SlaPlan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SlaPlanDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 128)
    private String name;

    @NotNull
    @Min(value = 1)
    private Integer firstResponseMins;

    @NotNull
    @Min(value = 5)
    private Integer resolutionMins;

    @NotNull
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFirstResponseMins() {
        return firstResponseMins;
    }

    public void setFirstResponseMins(Integer firstResponseMins) {
        this.firstResponseMins = firstResponseMins;
    }

    public Integer getResolutionMins() {
        return resolutionMins;
    }

    public void setResolutionMins(Integer resolutionMins) {
        this.resolutionMins = resolutionMins;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SlaPlanDTO)) {
            return false;
        }

        SlaPlanDTO slaPlanDTO = (SlaPlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, slaPlanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SlaPlanDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", firstResponseMins=" + getFirstResponseMins() +
            ", resolutionMins=" + getResolutionMins() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
