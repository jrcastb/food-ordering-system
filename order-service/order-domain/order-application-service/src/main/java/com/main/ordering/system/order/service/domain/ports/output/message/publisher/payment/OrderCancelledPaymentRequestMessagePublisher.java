package com.main.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import com.main.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.main.ordering.system.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {

}
