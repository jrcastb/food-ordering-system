package com.main.ordering.system.order.service.domain;

import com.main.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.main.ordering.system.order.service.domain.entity.Customer;
import com.main.ordering.system.order.service.domain.entity.Order;
import com.main.ordering.system.order.service.domain.entity.Restaurant;
import com.main.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.main.ordering.system.order.service.domain.exception.OrderDomainException;
import com.main.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.main.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.main.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.main.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderCreateHelper {
  private final OrderDomainService orderDomainService;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final RestaurantRepository restaurantRepository;
  private final OrderDataMapper orderDataMapper;

  public OrderCreateHelper(OrderDomainService orderDomainService, OrderRepository orderRepository,
      CustomerRepository customerRepository, RestaurantRepository restaurantRepository,
      OrderDataMapper orderDataMapper) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.restaurantRepository = restaurantRepository;
    this.orderDataMapper = orderDataMapper;
  }
  @Transactional
  public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand){
    checkCustomer(createOrderCommand.getCustomerId());
    Restaurant restaurant = checkRestaurant(createOrderCommand);
    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
    saveOrder(order);
    log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
    return orderCreatedEvent;
  }

  private void checkCustomer(final UUID customerId) {
    Optional<Customer> customer = customerRepository.findCustomer(customerId);
    if (customer.isEmpty()){
      log.warn("Could not find customer with customer id: {}", customerId);
      throw new OrderDomainException("Could not find customer with customer id: " + customerId);
    }
  }

  private Restaurant checkRestaurant(final CreateOrderCommand createOrderCommand) {
    Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
    Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
    if (optionalRestaurant.isEmpty()){
      log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
      throw new OrderDomainException("Could not find restaurant with restaurant id: " + createOrderCommand.getRestaurantId());
    }
    return optionalRestaurant.get();
  }

  private Order saveOrder(Order order){
    Order orderResult = orderRepository.save(order);
    if (orderResult == null){
      log.error("Could not save order!");
      throw new OrderDomainException("Could not save order!");
    }
    log.info("Order is saved with id: {}", orderResult.getId().getValue());
    return orderResult;
  }

}
