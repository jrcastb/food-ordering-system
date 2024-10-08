package com.main.ordering.system.domain.event.publisher;

import com.main.ordering.system.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
  void publish(T domainEvent);
}
