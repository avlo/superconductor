package com.prosilion.superconductor.lib.jpa.dto.classified;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PriceTag;
import com.prosilion.superconductor.lib.jpa.entity.classified.PriceTagEntity;

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

