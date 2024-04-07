package com.prosilion.nostrrelay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.event.tag.PriceTag;
import org.springframework.beans.BeanUtils;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "price_tag")
public class PriceTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String number;
  private String currency;
  private String frequency;

  public PriceTag convertEntityToDto() {
    PriceTag priceTag = PriceTag.builder().number(number).currency(currency).frequency(frequency).build();
    BeanUtils.copyProperties(priceTag, this);
    return priceTag;
  }
}
