package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.classified.PriceTagDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.event.BaseTag;
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

  public PriceTagEntity(BigDecimal number, String currency, String frequency) {
    this.number = number;
    this.currency = currency;
    this.frequency = frequency;
  }

  public BaseTag getAsBaseTag() {
    return new PriceTag(number, currency, frequency);
  }

  public PriceTagDto convertEntityToDto() {
    return new PriceTagDto(number, currency, frequency);
  }
}
