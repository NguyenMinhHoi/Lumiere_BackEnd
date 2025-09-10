package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.FileStatus;
import com.lumi.app.domain.enumeration.StorageType;
import jakarta.validation.constraints.*;
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

    private TicketDTO ticket;

    private UserDTO uploader;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    public UserDTO getUploader() {
        return uploader;
    }

    public void setUploader(UserDTO uploader) {
        this.uploader = uploader;
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
            ", ticket=" + getTicket() +
            ", uploader=" + getUploader() +
            "}";
    }
}
