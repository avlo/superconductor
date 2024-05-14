package com.prosilion.superconductor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.event.tag.PriceTag;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "price_tag")
public class PriceTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal number;
  private String currency;
  private String frequency;

  public PriceTag convertEntityToDto() {
    return PriceTag.builder().number(number).currency(currency).frequency(frequency).build();
  }
}
