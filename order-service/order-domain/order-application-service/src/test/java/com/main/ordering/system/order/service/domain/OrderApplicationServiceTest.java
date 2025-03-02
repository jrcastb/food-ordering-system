package com.main.ordering.system.order.service.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.main.ordering.system.domain.valueobject.CustomerId;
import com.main.ordering.system.domain.valueobject.Money;
import com.main.ordering.system.domain.valueobject.OrderId;
import com.main.ordering.system.domain.valueobject.OrderStatus;
import com.main.ordering.system.domain.valueobject.ProductId;
import com.main.ordering.system.domain.valueobject.RestaurantId;
import com.main.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.main.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.main.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.main.ordering.system.order.service.domain.dto.create.OrderItem;
import com.main.ordering.system.order.service.domain.entity.Customer;
import com.main.ordering.system.order.service.domain.entity.Order;
import com.main.ordering.system.order.service.domain.entity.Product;
import com.main.ordering.system.order.service.domain.entity.Restaurant;
import com.main.ordering.system.order.service.domain.exception.OrderDomainException;
import com.main.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.main.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.main.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.main.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.main.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

  @Autowired
  private OrderApplicationService orderApplicationService;
  @Autowired
  private OrderDataMapper orderDataMapper;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private RestaurantRepository restaurantRepository;

  private CreateOrderCommand createOrderCommand;
  private CreateOrderCommand createOrderCommandWrongPrice;
  private CreateOrderCommand createOrderCommandWrongProductPrice;
  private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
  private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
  private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
  private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
  private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
  private final BigDecimal PRICE = new BigDecimal("200.00");

  @BeforeAll
  public void init(){
    createOrderCommand = CreateOrderCommand.builder()
        .customerId(CUSTOMER_ID)
        .restaurantId(RESTAURANT_ID)
        .address(OrderAddress.builder()
            .street("street_1")
            .postalCode("1000AB")
            .city("Paris")
            .build())
        .price(PRICE)
        .items(List.of(OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(1)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("50.00"))
                .build(),
            OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(3)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("150.00"))
                .build()))
        .build();

    createOrderCommandWrongPrice = CreateOrderCommand.builder()
        .customerId(CUSTOMER_ID)
        .restaurantId(RESTAURANT_ID)
        .address(OrderAddress.builder()
            .street("street_1")
            .postalCode("1000AB")
            .city("Paris")
            .build())
        .price(new BigDecimal("250.00"))
        .items(List.of(OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(1)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("50.00"))
                .build(),
            OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(3)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("150.00"))
                .build()))
        .build();

    createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
        .customerId(CUSTOMER_ID)
        .restaurantId(RESTAURANT_ID)
        .address(OrderAddress.builder()
            .street("street_1")
            .postalCode("1000AB")
            .city("Paris")
            .build())
        .price(new BigDecimal("210.00"))
        .items(List.of(OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(1)
                .price(new BigDecimal("60.00"))
                .subTotal(new BigDecimal("60.00"))
                .build(),
            OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(3)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("150.00"))
                .build()))
        .build();

    Customer customer = new Customer();
    customer.setId(new CustomerId(CUSTOMER_ID));

    Restaurant restaurantResponse = Restaurant.Builder.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
            new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
        .active(true)
        .build();

    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));

    when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
  }
  @Test
  public void testCreateOrder(){
    CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
    assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
    assertEquals("Order Created Successfully", createOrderResponse.getMessage());
    assertNotNull(createOrderResponse.getOrderTrackingId());
  }
  @Test
  public void testCreateOrderWithWrongTotalPrice(){
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
    assertEquals("Total price: 250.00 isn't equal to Order items total: 200.00!", orderDomainException.getMessage());
  }

  @Test
  public void testCreateOrderWithWrongProductPrice(){
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
    assertEquals("Order item price: 60.00 isn't valid for product " + PRODUCT_ID,
        orderDomainException.getMessage());
  }

  @Test
  public void testCreateOrderWithPassiveRestaurant(){
    Restaurant restaurantResponse = Restaurant.Builder.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
            new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
        .active(false)
        .build();

    when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrder(createOrderCommand));
    assertEquals("Restaurant with id " + RESTAURANT_ID + " is currently not active!",
        orderDomainException.getMessage());
  }

}
