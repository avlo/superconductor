package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.classified.PriceTagDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  public PriceTagDto convertEntityToDto() {
    return new PriceTagDto(number, currency, frequency);
  }
}
