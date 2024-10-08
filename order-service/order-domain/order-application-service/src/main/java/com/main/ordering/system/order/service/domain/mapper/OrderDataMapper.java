package com.main.ordering.system.order.service.domain.mapper;

import com.main.ordering.system.domain.valueobject.CustomerId;
import com.main.ordering.system.domain.valueobject.Money;
import com.main.ordering.system.domain.valueobject.ProductId;
import com.main.ordering.system.domain.valueobject.RestaurantId;
import com.main.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.main.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.main.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.main.ordering.system.order.service.domain.entity.Order;
import com.main.ordering.system.order.service.domain.entity.OrderItem;
import com.main.ordering.system.order.service.domain.entity.Product;
import com.main.ordering.system.order.service.domain.entity.Restaurant;
import com.main.ordering.system.order.service.domain.valueobject.StreetAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderDataMapper {
  public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand){
    return Restaurant.Builder.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(createOrderCommand.getItems().stream().map(
            orderItem -> new Product(new ProductId(orderItem.getProductId()))
        ).collect(Collectors.toList()))
        .build();
  }

  public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand){
    return Order.Builder.builder()
        .customerId(new CustomerId(createOrderCommand.getCustomerId()))
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .deliveryAddress(orderAddessToStreetAddress(createOrderCommand.getAddress()))
        .price(new Money(createOrderCommand.getPrice()))
        .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
        .build();
  }

  public CreateOrderResponse orderToCreateOrderResponse(Order order){
    return CreateOrderResponse.builder()
        .orderTrackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .build();
  }

  private StreetAddress orderAddessToStreetAddress(final OrderAddress address) {
    return new StreetAddress(
        UUID.randomUUID(),
        address.getStreet(),
        address.getPostalCode(),
        address.getCity()
    );
  }

  private List<OrderItem> orderItemsToOrderItemEntities(final List<com.main.ordering.system.order.service.domain.dto.create.OrderItem> items) {
    return items.stream().map(
        orderItem -> OrderItem.Builder.builder()
            .product(new Product(new ProductId(orderItem.getProductId())))
            .price(new Money(orderItem.getPrice()))
            .quantity(orderItem.getQuantity())
            .subTotal(new Money(orderItem.getSubTotal()))
            .build()
    ).collect(Collectors.toList());
  }



}
