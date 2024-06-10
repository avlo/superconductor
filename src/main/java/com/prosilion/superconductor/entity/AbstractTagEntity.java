package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.entity.standard.Local;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class AbstractTagEntity implements Local {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
//  public abstract BaseTag getAsBaseTag();
}
