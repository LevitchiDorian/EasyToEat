package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import md.utm.restaurant.domain.enumeration.PaymentMethod;
import md.utm.restaurant.domain.enumeration.PaymentStatus;

/**
 * A DTO for the {@link md.utm.restaurant.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String transactionCode;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    @NotNull
    private PaymentStatus status;

    private Instant paidAt;

    @Size(max = 500)
    private String receiptUrl;

    @Size(max = 500)
    private String notes;

    @NotNull
    private Instant createdAt;

    private UserDTO processedBy;

    @NotNull
    private RestaurantOrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(UserDTO processedBy) {
        this.processedBy = processedBy;
    }

    public RestaurantOrderDTO getOrder() {
        return order;
    }

    public void setOrder(RestaurantOrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
            "id=" + getId() +
            ", transactionCode='" + getTransactionCode() + "'" +
            ", amount=" + getAmount() +
            ", method='" + getMethod() + "'" +
            ", status='" + getStatus() + "'" +
            ", paidAt='" + getPaidAt() + "'" +
            ", receiptUrl='" + getReceiptUrl() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", processedBy=" + getProcessedBy() +
            ", order=" + getOrder() +
            "}";
    }
}
