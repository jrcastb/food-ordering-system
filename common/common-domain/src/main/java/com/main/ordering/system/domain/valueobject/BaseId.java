package com.main.ordering.system.domain.valueobject;

import java.util.Objects;

public abstract class BaseId<T> {
  private final T value;

  protected BaseId(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final BaseId<?> baseId = (BaseId<?>) o;
    return Objects.equals(value, baseId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

}
