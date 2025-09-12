package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.FileStatus;
import com.lumi.app.domain.enumeration.StorageType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.TicketFile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketFileDTO implements Serializable {

    private Long id;

    @NotNull
    private Long ticketId;

    private Long uploaderId;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @Size(max = 255)
    private String originalName;

    @Size(max = 128)
    private String contentType;

    @NotNull
    @Min(value = 0L)
    private Long capacity;

    @NotNull
    private StorageType storageType;

    @Size(max = 512)
    private String path;

    @Size(max = 1024)
    private String url;

    @Size(max = 128)
    private String checksum;

    @NotNull
    private FileStatus status;

    @NotNull
    private Instant uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketFileDTO)) {
            return false;
        }

        TicketFileDTO ticketFileDTO = (TicketFileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketFileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketFileDTO{" +
            "id=" + getId() +
            ", ticketId=" + getTicketId() +
            ", uploaderId=" + getUploaderId() +
            ", fileName='" + getFileName() + "'" +
            ", originalName='" + getOriginalName() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", capacity=" + getCapacity() +
            ", storageType='" + getStorageType() + "'" +
            ", path='" + getPath() + "'" +
            ", url='" + getUrl() + "'" +
            ", checksum='" + getChecksum() + "'" +
            ", status='" + getStatus() + "'" +
            ", uploadedAt='" + getUploadedAt() + "'" +
            "}";
    }
}
