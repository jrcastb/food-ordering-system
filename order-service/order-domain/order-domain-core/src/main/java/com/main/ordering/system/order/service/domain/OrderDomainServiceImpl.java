package com.main.ordering.system.order.service.domain;

import com.main.ordering.system.domain.valueobject.ProductId;
import com.main.ordering.system.order.service.domain.entity.Order;
import com.main.ordering.system.order.service.domain.entity.Product;
import com.main.ordering.system.order.service.domain.entity.Restaurant;
import com.main.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.main.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.main.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.main.ordering.system.order.service.domain.exception.OrderDomainException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService{

  private static final String UTC = "UTC";
  @Override
  public OrderCreatedEvent validateAndInitiateOrder(Order order,
      Restaurant restaurant) {
    validateRestaurant(restaurant);
    setOrderProductInformation(order, restaurant);
    order.validateOrder();
    order.initializeOrder();
    log.info("Order with id: {} is initiated", order.getId().getValue());
    return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
  }

  private void validateRestaurant(Restaurant restaurant) {
    if (!restaurant.isActive()){
      throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() +
          " is currently not active!");
    }
  }

  private void setOrderProductInformation(Order order, Restaurant restaurant) {
    // Convertir la lista de productos del restaurante en un mapa, manejando duplicados
    Map<Product, Product> restaurantProductMap = restaurant.getProducts().stream()
        .collect(Collectors.toMap(
            Function.identity(),
            Function.identity(),
            (existing, replacement) -> existing // Mantener el producto existente
        ));

    // Actualizar la información de los productos del pedido
    order.getItems().forEach(orderItem -> {
      Product currentProduct = orderItem.getProduct();
      Product restaurantProduct = restaurantProductMap.get(currentProduct);

      if (restaurantProduct != null) {
        currentProduct.updateWithConfirmedNameAndPrice(
            restaurantProduct.getName(),
            restaurantProduct.getPrice());
      }
    });
  }

  @Override
  public OrderPaidEvent payOrder(Order order) {
    order.pay();
    log.info("Order with id: {} is paid", order.getId().getValue());
    return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
  }

  @Override
  public void approveOrder(Order order) {
    order.approve();
    log.info("Order with id: {} is approved", order.getId().getValue());
  }

  @Override
  public OrderCancelledEvent cancelOrderPayment(Order order,
      final List<String> failureMessages) {
    order.initCancel(failureMessages);
    log.info("Order payment is cancelling for order id: {}", order.getId().getValue());
    return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
  }

  @Override
  public void cancelOrder(final Order order, List<String> failureMessages) {
    order.cancel(failureMessages);
    log.info("Order with id: {} is cancelled", order.getId().getValue());
  }

}
