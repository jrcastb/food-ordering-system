package com.main.ordering.system.order.service.domain.entity;

import com.main.ordering.system.domain.entity.AggregateRoot;
import com.main.ordering.system.domain.valueobject.RestaurantId;
import java.util.List;

public class Restaurant extends AggregateRoot<RestaurantId> {
  private final List<Product> products;
  private boolean active;

  private Restaurant(final Builder builder) {
    super.setId(builder.restaurantId);
    products = builder.products;
    active = builder.active;
  }

  public List<Product> getProducts() {
    return products;
  }

  public boolean isActive() {
    return active;
  }

  public static final class Builder {

    private RestaurantId restaurantId;

    private List<Product> products;

    private boolean active;

    private Builder() {
    }

    public static Builder builder() {
      return new Builder();
    }

    public Builder restaurantId(final RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder products(final List<Product> val) {
      products = val;
      return this;
    }

    public Builder active(final boolean val) {
      active = val;
      return this;
    }

    public Restaurant build() {
      return new Restaurant(this);
    }

  }

}
