package com.prosilion.superconductor.dto.classified;

import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.base.ElementAttribute;
import nostr.event.tag.PriceTag;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class PriceTagDto extends PriceTag {

  public PriceTagDto(BigDecimal number, String currency, String frequency) {
    super(number, currency, frequency);
  }

  public PriceTagEntity convertDtoToEntity() {
    PriceTagEntity priceTagEntity = new PriceTagEntity();
    BeanUtils.copyProperties(this, priceTagEntity);
    return priceTagEntity;
  }

  public static PriceTagDto createPriceTagDtoFromAttributes(List<ElementAttribute> atts) {
//    TODO: refactor
    BigDecimal one = new BigDecimal(atts.get(0).getValue().toString());
    String two = atts.get(1).getValue().toString();
    String three = atts.get(2).getValue().toString();
    return new PriceTagDto(one, two, three);
  }
}
