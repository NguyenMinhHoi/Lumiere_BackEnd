package com.lumi.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.app.domain.enumeration.FileStatus;
import com.lumi.app.domain.enumeration.StorageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TicketFile.
 */
@Entity
@Table(name = "ticket_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ticketfile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "file_name", length = 255, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String fileName;

    @Size(max = 255)
    @Column(name = "original_name", length = 255)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String originalName;

    @Size(max = 128)
    @Column(name = "content_type", length = 128)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String contentType;

    @NotNull
    @Min(value = 0L)
    @Column(name = "capacity", nullable = false)
    private Long capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private StorageType storageType;

    @Size(max = 512)
    @Column(name = "path", length = 512)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String path;

    @Size(max = 1024)
    @Column(name = "url", length = 1024)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String url;

    @Size(max = 128)
    @Column(name = "checksum", length = 128)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String checksum;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private FileStatus status;

    @NotNull
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "assignee", "slaPlan", "order", "tags" }, allowSetters = true)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploader;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TicketFile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public TicketFile fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public TicketFile originalName(String originalName) {
        this.setOriginalName(originalName);
        return this;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public TicketFile contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getCapacity() {
        return this.capacity;
    }

    public TicketFile capacity(Long capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public StorageType getStorageType() {
        return this.storageType;
    }

    public TicketFile storageType(StorageType storageType) {
        this.setStorageType(storageType);
        return this;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public String getPath() {
        return this.path;
    }

    public TicketFile path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return this.url;
    }

    public TicketFile url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public TicketFile checksum(String checksum) {
        this.setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public FileStatus getStatus() {
        return this.status;
    }

    public TicketFile status(FileStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public TicketFile uploadedAt(Instant uploadedAt) {
        this.setUploadedAt(uploadedAt);
        return this;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public TicketFile ticket(Ticket ticket) {
        this.setTicket(ticket);
        return this;
    }

    public User getUploader() {
        return this.uploader;
    }

    public void setUploader(User user) {
        this.uploader = user;
    }

    public TicketFile uploader(User user) {
        this.setUploader(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketFile)) {
            return false;
        }
        return getId() != null && getId().equals(((TicketFile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketFile{" +
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
            "}";
    }
}
