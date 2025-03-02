package com.main.ordering.system.order.service.domain.ports.input.service.message.listener.payment;

import com.main.ordering.system.order.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {
  void paymentCompleted(PaymentResponse paymentResponse);
  void paymentCancelled(PaymentResponse paymentResponse);
}
