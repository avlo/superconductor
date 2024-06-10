package com.prosilion.superconductor.dto.classified;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import lombok.Getter;
import nostr.base.ElementAttribute;
import nostr.event.tag.PriceTag;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class PriceTagDto implements StandardTagDto {
  private final PriceTag priceTag;

  public PriceTagDto(PriceTag priceTag) {
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

  public static PriceTagDto createPriceTagDtoFromAttributes(List<ElementAttribute> atts) {
//    TODO: refactor
    BigDecimal one = new BigDecimal(atts.get(0).getValue().toString());
    String two = atts.get(1).getValue().toString();
    String three = atts.get(2).getValue().toString();
    return new PriceTagDto(new PriceTag(one, two, three));
  }
}
