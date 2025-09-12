package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.FulfillmentStatus;
import com.lumi.app.domain.enumeration.OrderStatus;
import com.lumi.app.domain.enumeration.PaymentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.Orders} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrdersDTO implements Serializable {

    private Long id;

    private Long customerId;

    @NotNull
    @Size(min = 6, max = 32)
    private String code;

    @NotNull
    private OrderStatus status;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    private FulfillmentStatus fulfillmentStatus;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalAmount;

    @Size(max = 3)
    private String currency;

    @Size(max = 500)
    private String note;

    private Instant placedAt;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public FulfillmentStatus getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    public void setFulfillmentStatus(FulfillmentStatus fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Instant placedAt) {
        this.placedAt = placedAt;
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
        if (!(o instanceof OrdersDTO)) {
            return false;
        }

        OrdersDTO ordersDTO = (OrdersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ordersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdersDTO{" +
            "id=" + getId() +
            ", customerId=" + getCustomerId() +
            ", code='" + getCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", fulfillmentStatus='" + getFulfillmentStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", currency='" + getCurrency() + "'" +
            ", note='" + getNote() + "'" +
            ", placedAt='" + getPlacedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
