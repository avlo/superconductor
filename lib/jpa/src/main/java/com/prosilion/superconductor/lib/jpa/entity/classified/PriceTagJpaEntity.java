package com.prosilion.superconductor.lib.jpa.entity.classified;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.classified.PriceTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.PriceTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "price_tag")
// TODO: comprehensive unit test all parameter variants
public class PriceTagJpaEntity extends AbstractTagJpaEntity {
  private BigDecimal number;
  private String currency;
  private String frequency;

  public PriceTagJpaEntity(@NonNull PriceTag priceTag) {
    super("price");
    this.number = priceTag.getNumber();
    this.currency = priceTag.getCurrency();
    this.frequency = priceTag.getFrequency();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new PriceTag(number, currency, frequency);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new PriceTagDto(new PriceTag(number, currency, frequency));
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(number.toString(),
        Optional.ofNullable(currency).toString(),
        Optional.ofNullable(frequency).toString());
  }
}
