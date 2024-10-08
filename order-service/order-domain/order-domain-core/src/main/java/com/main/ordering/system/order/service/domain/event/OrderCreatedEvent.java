package com.main.ordering.system.order.service.domain.event;

import com.main.ordering.system.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {

  public OrderCreatedEvent(final Order order, final ZonedDateTime createdAt) {
    super(order, createdAt);
  }

}
