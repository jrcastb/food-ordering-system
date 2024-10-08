package com.main.ordering.system.domain.valueobject;

import java.util.UUID;

public class ProductId extends BaseId<UUID>{

  public ProductId(final UUID value) {
    super(value);
  }

}
