package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public abstract class AbstractFilterType implements Serializable {
  private Long id;
  private Long filterId;

  protected AbstractFilterType(Long filterId) {
    this.filterId = filterId;
  }

  public abstract boolean equals(Object o);
  public abstract int hashCode();
  public abstract String getCode();
}
