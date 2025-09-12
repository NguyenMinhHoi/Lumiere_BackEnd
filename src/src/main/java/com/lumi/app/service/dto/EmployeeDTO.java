package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.EmployeeRole;
import com.lumi.app.domain.enumeration.EmployeeStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.Employee} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    private String code;

    @NotNull
    @Size(min = 2, max = 128)
    private String fullName;

    @NotNull
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    private String email;

    @Pattern(regexp = "^[0-9+()\\-\\s]{6,20}$")
    private String phone;

    @NotNull
    private EmployeeRole role;

    @NotNull
    private EmployeeStatus status;

    @Size(max = 128)
    private String department;

    private Instant joinedAt;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
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
        if (!(o instanceof EmployeeDTO)) {
            return false;
        }

        EmployeeDTO employeeDTO = (EmployeeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, employeeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", department='" + getDepartment() + "'" +
            ", joinedAt='" + getJoinedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
