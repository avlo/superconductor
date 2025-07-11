package com.prosilion.superconductor.dto.classified;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PriceTag;

@Getter
public class PriceTagDto implements AbstractTagDto {
  private final PriceTag priceTag;

  public PriceTagDto(@NonNull PriceTag priceTag) {
    this.priceTag = priceTag;
  }

  @Override
  public String getCode() {
    return priceTag.getCode();
  }

  @Override
  public PriceTagEntity convertDtoToEntity() {
    return new PriceTagEntity(priceTag);
  }
}

