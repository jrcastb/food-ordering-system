package com.main.ordering.system.order.service.domain.valueobject;

import com.main.ordering.system.domain.valueobject.BaseId;
import java.util.UUID;

public class TrackingId extends BaseId<UUID> {

  public TrackingId(final UUID value) {
    super(value);
  }

}
