package com.main.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.main.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.main.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
