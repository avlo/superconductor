package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractFilterType implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long filterId;

  protected AbstractFilterType(Long filterId) {
    this.filterId = filterId;
  }

  public abstract boolean equals(Object o);
  public abstract int hashCode();
  public abstract String getCode();
}
