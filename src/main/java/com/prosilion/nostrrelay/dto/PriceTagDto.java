package com.prosilion.nostrrelay.dto;

import com.prosilion.nostrrelay.entity.BaseTagEntity;
import com.prosilion.nostrrelay.entity.PriceTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.tag.PriceTag;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

@Setter
@Getter
public class PriceTagDto extends PriceTag {

  public PriceTagDto(String price, String number, String currency, String frequency) {
    super(price, number, currency, frequency);
  }

  public PriceTagEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    PriceTagEntity priceTagEntity = new PriceTagEntity();
    BeanUtils.copyProperties(priceTagEntity, this);
    return priceTagEntity;
  }
}
