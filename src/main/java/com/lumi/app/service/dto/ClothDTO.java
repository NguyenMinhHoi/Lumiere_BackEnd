package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.ClothStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.Cloth} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClothDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    private String code;

    @NotNull
    @Size(min = 2, max = 128)
    private String name;

    @Size(max = 128)
    private String material;

    @Size(max = 64)
    private String color;

    private Double width;

    private Double length;

    @Size(max = 16)
    private String unit;

    @NotNull
    private ClothStatus status;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ClothStatus getStatus() {
        return status;
    }

    public void setStatus(ClothStatus status) {
        this.status = status;
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
        if (!(o instanceof ClothDTO)) {
            return false;
        }

        ClothDTO clothDTO = (ClothDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clothDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClothDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", material='" + getMaterial() + "'" +
            ", color='" + getColor() + "'" +
            ", width=" + getWidth() +
            ", length=" + getLength() +
            ", unit='" + getUnit() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
