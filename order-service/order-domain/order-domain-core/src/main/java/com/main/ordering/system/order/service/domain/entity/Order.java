package com.main.ordering.system.order.service.domain.entity;

import com.main.ordering.system.domain.entity.AggregateRoot;
import com.main.ordering.system.domain.valueobject.CustomerId;
import com.main.ordering.system.domain.valueobject.Money;
import com.main.ordering.system.domain.valueobject.OrderId;
import com.main.ordering.system.domain.valueobject.OrderStatus;
import com.main.ordering.system.domain.valueobject.RestaurantId;
import com.main.ordering.system.order.service.domain.exception.OrderDomainException;
import com.main.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.main.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.main.ordering.system.order.service.domain.valueobject.TrackingId;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
  private final CustomerId customerId;
  private final RestaurantId restaurantId;
  private final StreetAddress deliveryAddress;
  private final Money price;
  private final List<OrderItem> items;
  private TrackingId trackingId;
  private OrderStatus orderStatus;
  private List<String> failureMessages;

  public void initializeOrder(){
    setId(new OrderId(UUID.randomUUID()));
    trackingId = new TrackingId(UUID.randomUUID());
    orderStatus = OrderStatus.PENDING;
    initializeOrderItems();
  }

  public void validateOrder(){
    validateInitialOrder();
    validateTotalPrice();
    validateItemsPrice();
  }

  public void pay(){
    if (orderStatus != OrderStatus.PENDING){
      throw new OrderDomainException("Order isn't in correct state for pay operation!");
    }
    orderStatus = OrderStatus.PAID;
  }

  public void approve(){
    if (orderStatus != OrderStatus.PAID){
      throw new OrderDomainException("Order isn't in correct stat for approve operation!");
    }
    orderStatus = OrderStatus.APPROVED;
  }

  public void initCancel(List<String> failureMessages){
    if (orderStatus != OrderStatus.PAID){
      throw new OrderDomainException("Order isn't in correct state for init cancel operation!");
    }
    orderStatus = OrderStatus.CANCELLING;
    updateFailureMessages(failureMessages);
  }

  public void cancel(List<String> failureMessages){
    if (!(orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.CANCELLING)){
      throw new OrderDomainException("Order ins't in correct state for cancel operation!");
    }
    orderStatus = OrderStatus.CANCELLED;
    updateFailureMessages(failureMessages);
  }

  private void updateFailureMessages(final List<String> failureMessages) {
    if (this.failureMessages != null && failureMessages != null){
      this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
    }
    if (this.failureMessages == null){
      this.failureMessages = failureMessages;
    }
  }

  private void validateInitialOrder() {
    if (this.orderStatus != null || this.getId() != null){
      throw new OrderDomainException("Order isn't in correct state for initialization!");
    }
  }

  private void validateTotalPrice() {
    if (this.price == null || !this.price.isGreaterThanZero()){
      throw new OrderDomainException("Total price must be greater than zero!");
    }
  }

  private void validateItemsPrice() {
    Money orderItemsTotal = items.stream().map(orderItem -> {
      validateItemPrice(orderItem);
      return orderItem.getSubTotal();
    }).reduce(Money.ZERO, Money::add);
    if (!price.equals(orderItemsTotal)){
      throw new OrderDomainException("Total price: " + price.getAmount()
      + " isn't equal to Order items total: " + orderItemsTotal.getAmount() + "!");
    }
  }

  private void validateItemPrice(final OrderItem orderItem) {
    if (!orderItem.isPriceValid()){
      throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
          " isn't valid for product " + orderItem.getProduct().getId().getValue());
    }
  }

  private void initializeOrderItems() {
    long itemId = 1;
    for (OrderItem orderItem: this.items){
      orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
    }
  }

  private Order(final Builder builder) {
    super.setId(builder.orderId);
    customerId = builder.customerId;
    restaurantId = builder.restaurantId;
    deliveryAddress = builder.deliveryAddress;
    price = builder.price;
    items = builder.items;
    trackingId = builder.trackingId;
    orderStatus = builder.orderStatus;
    failureMessages = builder.failureMessages;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public RestaurantId getRestaurantId() {
    return restaurantId;
  }

  public StreetAddress getDeliveryAddress() {
    return deliveryAddress;
  }

  public Money getPrice() {
    return price;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public TrackingId getTrackingId() {
    return trackingId;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public List<String> getFailureMessages() {
    return failureMessages;
  }

  public static final class Builder {

    private OrderId orderId;

    private CustomerId customerId;

    private RestaurantId restaurantId;

    private StreetAddress deliveryAddress;

    private Money price;

    private List<OrderItem> items;

    private TrackingId trackingId;

    private OrderStatus orderStatus;

    private List<String> failureMessages;

    private Builder() {
    }

    public static Builder builder() {
      return new Builder();
    }

    public Builder orderId(final OrderId val) {
      orderId = val;
      return this;
    }

    public Builder customerId(final CustomerId val) {
      customerId = val;
      return this;
    }

    public Builder restaurantId(final RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder deliveryAddress(final StreetAddress val) {
      deliveryAddress = val;
      return this;
    }

    public Builder price(final Money val) {
      price = val;
      return this;
    }

    public Builder items(final List<OrderItem> val) {
      items = val;
      return this;
    }

    public Builder trackingId(final TrackingId val) {
      trackingId = val;
      return this;
    }

    public Builder orderStatus(final OrderStatus val) {
      orderStatus = val;
      return this;
    }

    public Builder failureMessages(final List<String> val) {
      failureMessages = val;
      return this;
    }

    public Order build() {
      return new Order(this);
    }

  }

}
