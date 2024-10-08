package com.main.ordering.system.order.service.domain;

import com.main.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.main.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.main.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.main.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.main.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {

  private final OrderCreateCommandHandler orderCreateCommandHandler;
  private final OrderTrackCommandHandler orderTrackCommandHandler;

  OrderApplicationServiceImpl(OrderCreateCommandHandler orderCreateCommandHandler,
      OrderTrackCommandHandler orderTrackCommandHandler) {
    this.orderCreateCommandHandler = orderCreateCommandHandler;
    this.orderTrackCommandHandler = orderTrackCommandHandler;
  }

  @Override
  public CreateOrderResponse createOrder(final CreateOrderCommand createOrderCommand) {
    return orderCreateCommandHandler.createOrder(createOrderCommand);
  }

  @Override
  public TrackOrderResponse trackOrder(final TrackOrderQuery trackOrderQuery) {
    return orderTrackCommandHandler.trackOrder(trackOrderQuery);
  }

}
