package com.main.ordering.system.order.service.domain.entity;

import com.main.ordering.system.domain.entity.BaseEntity;
import com.main.ordering.system.domain.valueobject.Money;
import com.main.ordering.system.domain.valueobject.OrderId;
import com.main.ordering.system.order.service.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
  private OrderId orderId;
  private final Product product;
  private final int quantity;
  private final Money price;
  private final Money subTotal;

  private OrderItem(final Builder builder) {
    super.setId(builder.orderItemId);
    product = builder.product;
    quantity = builder.quantity;
    price = builder.price;
    subTotal = builder.subTotal;
  }

  void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
    this.orderId = orderId;
    super.setId(orderItemId);
  }

  boolean isPriceValid(){
    return price.isGreaterThanZero() &&
        price.equals(product.getPrice()) &&
        price.multiply(quantity).equals(subTotal);
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public Money getPrice() {
    return price;
  }

  public Money getSubTotal() {
    return subTotal;
  }

  public static final class Builder {

    private OrderItemId orderItemId;

    private Product product;

    private int quantity;

    private Money price;

    private Money subTotal;

    private Builder() {
    }

    public static Builder builder() {
      return new Builder();
    }

    public Builder orderItemId(final OrderItemId val) {
      orderItemId = val;
      return this;
    }

    public Builder product(final Product val) {
      product = val;
      return this;
    }

    public Builder quantity(final int val) {
      quantity = val;
      return this;
    }

    public Builder price(final Money val) {
      price = val;
      return this;
    }

    public Builder subTotal(final Money val) {
      subTotal = val;
      return this;
    }

    public OrderItem build() {
      return new OrderItem(this);
    }

  }

}
