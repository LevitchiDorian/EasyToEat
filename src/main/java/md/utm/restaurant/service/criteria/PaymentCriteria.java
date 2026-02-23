package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import md.utm.restaurant.domain.enumeration.PaymentMethod;
import md.utm.restaurant.domain.enumeration.PaymentStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.Payment} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.PaymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PaymentMethod
     */
    public static class PaymentMethodFilter extends Filter<PaymentMethod> {

        public PaymentMethodFilter() {}

        public PaymentMethodFilter(PaymentMethodFilter filter) {
            super(filter);
        }

        @Override
        public PaymentMethodFilter copy() {
            return new PaymentMethodFilter(this);
        }
    }

    /**
     * Class for filtering PaymentStatus
     */
    public static class PaymentStatusFilter extends Filter<PaymentStatus> {

        public PaymentStatusFilter() {}

        public PaymentStatusFilter(PaymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public PaymentStatusFilter copy() {
            return new PaymentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter transactionCode;

    private BigDecimalFilter amount;

    private PaymentMethodFilter method;

    private PaymentStatusFilter status;

    private InstantFilter paidAt;

    private StringFilter receiptUrl;

    private StringFilter notes;

    private InstantFilter createdAt;

    private LongFilter processedById;

    private LongFilter orderId;

    private Boolean distinct;

    public PaymentCriteria() {}

    public PaymentCriteria(PaymentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.transactionCode = other.optionalTransactionCode().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.method = other.optionalMethod().map(PaymentMethodFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(PaymentStatusFilter::copy).orElse(null);
        this.paidAt = other.optionalPaidAt().map(InstantFilter::copy).orElse(null);
        this.receiptUrl = other.optionalReceiptUrl().map(StringFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.processedById = other.optionalProcessedById().map(LongFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaymentCriteria copy() {
        return new PaymentCriteria(this);
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

    public StringFilter getTransactionCode() {
        return transactionCode;
    }

    public Optional<StringFilter> optionalTransactionCode() {
        return Optional.ofNullable(transactionCode);
    }

    public StringFilter transactionCode() {
        if (transactionCode == null) {
            setTransactionCode(new StringFilter());
        }
        return transactionCode;
    }

    public void setTransactionCode(StringFilter transactionCode) {
        this.transactionCode = transactionCode;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public PaymentMethodFilter getMethod() {
        return method;
    }

    public Optional<PaymentMethodFilter> optionalMethod() {
        return Optional.ofNullable(method);
    }

    public PaymentMethodFilter method() {
        if (method == null) {
            setMethod(new PaymentMethodFilter());
        }
        return method;
    }

    public void setMethod(PaymentMethodFilter method) {
        this.method = method;
    }

    public PaymentStatusFilter getStatus() {
        return status;
    }

    public Optional<PaymentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public PaymentStatusFilter status() {
        if (status == null) {
            setStatus(new PaymentStatusFilter());
        }
        return status;
    }

    public void setStatus(PaymentStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getPaidAt() {
        return paidAt;
    }

    public Optional<InstantFilter> optionalPaidAt() {
        return Optional.ofNullable(paidAt);
    }

    public InstantFilter paidAt() {
        if (paidAt == null) {
            setPaidAt(new InstantFilter());
        }
        return paidAt;
    }

    public void setPaidAt(InstantFilter paidAt) {
        this.paidAt = paidAt;
    }

    public StringFilter getReceiptUrl() {
        return receiptUrl;
    }

    public Optional<StringFilter> optionalReceiptUrl() {
        return Optional.ofNullable(receiptUrl);
    }

    public StringFilter receiptUrl() {
        if (receiptUrl == null) {
            setReceiptUrl(new StringFilter());
        }
        return receiptUrl;
    }

    public void setReceiptUrl(StringFilter receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getProcessedById() {
        return processedById;
    }

    public Optional<LongFilter> optionalProcessedById() {
        return Optional.ofNullable(processedById);
    }

    public LongFilter processedById() {
        if (processedById == null) {
            setProcessedById(new LongFilter());
        }
        return processedById;
    }

    public void setProcessedById(LongFilter processedById) {
        this.processedById = processedById;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
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
        final PaymentCriteria that = (PaymentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(transactionCode, that.transactionCode) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(method, that.method) &&
            Objects.equals(status, that.status) &&
            Objects.equals(paidAt, that.paidAt) &&
            Objects.equals(receiptUrl, that.receiptUrl) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(processedById, that.processedById) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            transactionCode,
            amount,
            method,
            status,
            paidAt,
            receiptUrl,
            notes,
            createdAt,
            processedById,
            orderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTransactionCode().map(f -> "transactionCode=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalMethod().map(f -> "method=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPaidAt().map(f -> "paidAt=" + f + ", ").orElse("") +
            optionalReceiptUrl().map(f -> "receiptUrl=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalProcessedById().map(f -> "processedById=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
