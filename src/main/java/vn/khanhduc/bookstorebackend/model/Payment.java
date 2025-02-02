package vn.khanhduc.bookstorebackend.model;

import jakarta.persistence.*;
import lombok.*;
import vn.khanhduc.bookstorebackend.common.PaymentMethod;
import vn.khanhduc.bookstorebackend.common.PaymentStatus;
import java.math.BigDecimal;

@Entity(name = "Payment")
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment extends AbstractEntity<Long>{

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
