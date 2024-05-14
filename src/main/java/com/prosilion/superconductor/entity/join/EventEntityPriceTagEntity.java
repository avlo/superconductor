package com.prosilion.superconductor.entity.join;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-price_tag-join")
public class EventEntityPriceTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long priceTagId;

  public EventEntityPriceTagEntity(Long eventId, Long priceTagId) {
    this.eventId = eventId;
    this.priceTagId = priceTagId;
  }
}