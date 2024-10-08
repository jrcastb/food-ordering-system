package com.main.ordering.system.order.service.domain.event;

import com.main.ordering.system.domain.event.DomainEvent;
import com.main.ordering.system.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {

  public OrderCancelledEvent(final Order order, final ZonedDateTime createdAt) {
    super(order, createdAt);
  }

}
