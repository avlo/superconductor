package com.prosilion.superconductor.entity.join.standard;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class EventEntityStandardTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long eventId;

  public abstract Long getLookupId();
}