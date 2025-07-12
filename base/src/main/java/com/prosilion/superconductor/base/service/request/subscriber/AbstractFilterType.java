package com.prosilion.superconductor.base.service.request.subscriber;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public abstract class AbstractFilterType implements Supplier<Object>, Serializable {
  private Long id;
  private Long filterId;
  private String code;

  protected AbstractFilterType(Long filterId, String code) {
    this.filterId = filterId;
    this.code = code;
  }

  @Override
  public int hashCode() {
    return Objects.hash(get());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractFilterType that = (AbstractFilterType) o;
    return Objects.equals(get(), that.get());
  }
}
