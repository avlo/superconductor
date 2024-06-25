package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.AbstractTagDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import nostr.event.BaseTag;

import java.io.Serializable;

@Setter
@Getter
@MappedSuperclass
public abstract class AbstractTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract String getCode();
  public abstract AbstractTagDto convertEntityToDto();
  public abstract BaseTag getAsBaseTag();

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();
}
