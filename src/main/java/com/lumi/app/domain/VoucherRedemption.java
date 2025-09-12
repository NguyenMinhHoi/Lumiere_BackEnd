package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A VoucherRedemption.
 */
@Entity
@Table(name = "voucher_redemption")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "voucherredemption")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VoucherRedemption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @NotNull
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotNull
    @Column(name = "redeemed_at", nullable = false)
    private Instant redeemedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VoucherRedemption id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoucherId() {
        return this.voucherId;
    }

    public VoucherRedemption voucherId(Long voucherId) {
        this.setVoucherId(voucherId);
        return this;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public VoucherRedemption orderId(Long orderId) {
        this.setOrderId(orderId);
        return this;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public VoucherRedemption customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Instant getRedeemedAt() {
        return this.redeemedAt;
    }

    public VoucherRedemption redeemedAt(Instant redeemedAt) {
        this.setRedeemedAt(redeemedAt);
        return this;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VoucherRedemption)) {
            return false;
        }
        return getId() != null && getId().equals(((VoucherRedemption) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VoucherRedemption{" +
            "id=" + getId() +
            ", voucherId=" + getVoucherId() +
            ", orderId=" + getOrderId() +
            ", customerId=" + getCustomerId() +
            ", redeemedAt='" + getRedeemedAt() + "'" +
            "}";
    }
}
