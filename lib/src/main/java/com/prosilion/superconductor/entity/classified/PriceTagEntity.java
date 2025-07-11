package com.prosilion.superconductor.entity.classified;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.classified.PriceTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;



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
@RedisHash("price_tag")
// TODO: comprehensive unit test all parameter variants
public class PriceTagEntity extends AbstractTagEntity {
  private BigDecimal number;
  private String currency;
  private String frequency;

  public PriceTagEntity(@NonNull PriceTag priceTag) {
    super("price");
    this.number = priceTag.getNumber();
    this.currency = priceTag.getCurrency();
    this.frequency = priceTag.getFrequency();
  }

  @Override
  @org.springframework.data.annotation.Transient
  public BaseTag getAsBaseTag() {
    return new PriceTag(number, currency, frequency);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new PriceTagDto(new PriceTag(number, currency, frequency));
  }

  @Override
  @org.springframework.data.annotation.Transient
  public List<String> get() {
    return List.of(number.toString(),
        Optional.ofNullable(currency).toString(),
        Optional.ofNullable(frequency).toString());
  }
}
