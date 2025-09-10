package com.lumi.app.service.criteria;

import com.lumi.app.domain.enumeration.FileStatus;
import com.lumi.app.domain.enumeration.StorageType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.app.domain.TicketFile} entity. This class is used
 * in {@link com.lumi.app.web.rest.TicketFileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ticket-files?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketFileCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StorageType
     */
    public static class StorageTypeFilter extends Filter<StorageType> {

        public StorageTypeFilter() {}

        public StorageTypeFilter(StorageTypeFilter filter) {
            super(filter);
        }

        @Override
        public StorageTypeFilter copy() {
            return new StorageTypeFilter(this);
        }
    }

    /**
     * Class for filtering FileStatus
     */
    public static class FileStatusFilter extends Filter<FileStatus> {

        public FileStatusFilter() {}

        public FileStatusFilter(FileStatusFilter filter) {
            super(filter);
        }

        @Override
        public FileStatusFilter copy() {
            return new FileStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fileName;

    private StringFilter originalName;

    private StringFilter contentType;

    private LongFilter capacity;

    private StorageTypeFilter storageType;

    private StringFilter path;

    private StringFilter url;

    private StringFilter checksum;

    private FileStatusFilter status;

    private InstantFilter uploadedAt;

    private LongFilter ticketId;

    private LongFilter uploaderId;

    private Boolean distinct;

    public TicketFileCriteria() {}

    public TicketFileCriteria(TicketFileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fileName = other.optionalFileName().map(StringFilter::copy).orElse(null);
        this.originalName = other.optionalOriginalName().map(StringFilter::copy).orElse(null);
        this.contentType = other.optionalContentType().map(StringFilter::copy).orElse(null);
        this.capacity = other.optionalCapacity().map(LongFilter::copy).orElse(null);
        this.storageType = other.optionalStorageType().map(StorageTypeFilter::copy).orElse(null);
        this.path = other.optionalPath().map(StringFilter::copy).orElse(null);
        this.url = other.optionalUrl().map(StringFilter::copy).orElse(null);
        this.checksum = other.optionalChecksum().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(FileStatusFilter::copy).orElse(null);
        this.uploadedAt = other.optionalUploadedAt().map(InstantFilter::copy).orElse(null);
        this.ticketId = other.optionalTicketId().map(LongFilter::copy).orElse(null);
        this.uploaderId = other.optionalUploaderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TicketFileCriteria copy() {
        return new TicketFileCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public Optional<StringFilter> optionalFileName() {
        return Optional.ofNullable(fileName);
    }

    public StringFilter fileName() {
        if (fileName == null) {
            setFileName(new StringFilter());
        }
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public StringFilter getOriginalName() {
        return originalName;
    }

    public Optional<StringFilter> optionalOriginalName() {
        return Optional.ofNullable(originalName);
    }

    public StringFilter originalName() {
        if (originalName == null) {
            setOriginalName(new StringFilter());
        }
        return originalName;
    }

    public void setOriginalName(StringFilter originalName) {
        this.originalName = originalName;
    }

    public StringFilter getContentType() {
        return contentType;
    }

    public Optional<StringFilter> optionalContentType() {
        return Optional.ofNullable(contentType);
    }

    public StringFilter contentType() {
        if (contentType == null) {
            setContentType(new StringFilter());
        }
        return contentType;
    }

    public void setContentType(StringFilter contentType) {
        this.contentType = contentType;
    }

    public LongFilter getCapacity() {
        return capacity;
    }

    public Optional<LongFilter> optionalCapacity() {
        return Optional.ofNullable(capacity);
    }

    public LongFilter capacity() {
        if (capacity == null) {
            setCapacity(new LongFilter());
        }
        return capacity;
    }

    public void setCapacity(LongFilter capacity) {
        this.capacity = capacity;
    }

    public StorageTypeFilter getStorageType() {
        return storageType;
    }

    public Optional<StorageTypeFilter> optionalStorageType() {
        return Optional.ofNullable(storageType);
    }

    public StorageTypeFilter storageType() {
        if (storageType == null) {
            setStorageType(new StorageTypeFilter());
        }
        return storageType;
    }

    public void setStorageType(StorageTypeFilter storageType) {
        this.storageType = storageType;
    }

    public StringFilter getPath() {
        return path;
    }

    public Optional<StringFilter> optionalPath() {
        return Optional.ofNullable(path);
    }

    public StringFilter path() {
        if (path == null) {
            setPath(new StringFilter());
        }
        return path;
    }

    public void setPath(StringFilter path) {
        this.path = path;
    }

    public StringFilter getUrl() {
        return url;
    }

    public Optional<StringFilter> optionalUrl() {
        return Optional.ofNullable(url);
    }

    public StringFilter url() {
        if (url == null) {
            setUrl(new StringFilter());
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getChecksum() {
        return checksum;
    }

    public Optional<StringFilter> optionalChecksum() {
        return Optional.ofNullable(checksum);
    }

    public StringFilter checksum() {
        if (checksum == null) {
            setChecksum(new StringFilter());
        }
        return checksum;
    }

    public void setChecksum(StringFilter checksum) {
        this.checksum = checksum;
    }

    public FileStatusFilter getStatus() {
        return status;
    }

    public Optional<FileStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public FileStatusFilter status() {
        if (status == null) {
            setStatus(new FileStatusFilter());
        }
        return status;
    }

    public void setStatus(FileStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getUploadedAt() {
        return uploadedAt;
    }

    public Optional<InstantFilter> optionalUploadedAt() {
        return Optional.ofNullable(uploadedAt);
    }

    public InstantFilter uploadedAt() {
        if (uploadedAt == null) {
            setUploadedAt(new InstantFilter());
        }
        return uploadedAt;
    }

    public void setUploadedAt(InstantFilter uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LongFilter getTicketId() {
        return ticketId;
    }

    public Optional<LongFilter> optionalTicketId() {
        return Optional.ofNullable(ticketId);
    }

    public LongFilter ticketId() {
        if (ticketId == null) {
            setTicketId(new LongFilter());
        }
        return ticketId;
    }

    public void setTicketId(LongFilter ticketId) {
        this.ticketId = ticketId;
    }

    public LongFilter getUploaderId() {
        return uploaderId;
    }

    public Optional<LongFilter> optionalUploaderId() {
        return Optional.ofNullable(uploaderId);
    }

    public LongFilter uploaderId() {
        if (uploaderId == null) {
            setUploaderId(new LongFilter());
        }
        return uploaderId;
    }

    public void setUploaderId(LongFilter uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TicketFileCriteria that = (TicketFileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(originalName, that.originalName) &&
            Objects.equals(contentType, that.contentType) &&
            Objects.equals(capacity, that.capacity) &&
            Objects.equals(storageType, that.storageType) &&
            Objects.equals(path, that.path) &&
            Objects.equals(url, that.url) &&
            Objects.equals(checksum, that.checksum) &&
            Objects.equals(status, that.status) &&
            Objects.equals(uploadedAt, that.uploadedAt) &&
            Objects.equals(ticketId, that.ticketId) &&
            Objects.equals(uploaderId, that.uploaderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            fileName,
            originalName,
            contentType,
            capacity,
            storageType,
            path,
            url,
            checksum,
            status,
            uploadedAt,
            ticketId,
            uploaderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketFileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFileName().map(f -> "fileName=" + f + ", ").orElse("") +
            optionalOriginalName().map(f -> "originalName=" + f + ", ").orElse("") +
            optionalContentType().map(f -> "contentType=" + f + ", ").orElse("") +
            optionalCapacity().map(f -> "capacity=" + f + ", ").orElse("") +
            optionalStorageType().map(f -> "storageType=" + f + ", ").orElse("") +
            optionalPath().map(f -> "path=" + f + ", ").orElse("") +
            optionalUrl().map(f -> "url=" + f + ", ").orElse("") +
            optionalChecksum().map(f -> "checksum=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalUploadedAt().map(f -> "uploadedAt=" + f + ", ").orElse("") +
            optionalTicketId().map(f -> "ticketId=" + f + ", ").orElse("") +
            optionalUploaderId().map(f -> "uploaderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
