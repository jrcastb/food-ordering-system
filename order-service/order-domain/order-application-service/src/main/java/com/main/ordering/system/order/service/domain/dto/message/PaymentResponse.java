package com.main.ordering.system.order.service.domain.dto.message;

import com.main.ordering.system.domain.valueobject.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {
  private String id;
  private String sagaId;
  private String orderId;
  private String paymentId;
  private String customerId;
  private BigDecimal price;
  private Instant createdAt;
  private PaymentStatus paymentStatus;
  private List<String> failureMessages;
}
