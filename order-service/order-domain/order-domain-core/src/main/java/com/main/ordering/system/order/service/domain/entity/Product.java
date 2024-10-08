package com.main.ordering.system.order.service.domain.entity;

import com.main.ordering.system.domain.entity.BaseEntity;
import com.main.ordering.system.domain.valueobject.Money;
import com.main.ordering.system.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
  private String name;
  private Money price;

  public Product(ProductId productId, final String name, final Money price) {
    super.setId(productId);
    this.name = name;
    this.price = price;
  }
  public Product(ProductId productId){
    super.setId(productId);
  }
  public void updateWithConfirmedNameAndPrice(String name, Money price){
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public Money getPrice() {
    return price;
  }

}
