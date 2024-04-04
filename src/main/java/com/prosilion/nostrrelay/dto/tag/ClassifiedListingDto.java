package com.prosilion.nostrrelay.dto.tag;

import com.prosilion.nostrrelay.entity.BaseTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.tag.PriceTag;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Setter
@Getter
public class ClassifiedListingDto extends ClassifiedListing {

  public ClassifiedListingDto(String title, String summary, List<PriceTag> priceTags) {
    super(title, summary, priceTags);
  }

  public BaseTagEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    BaseTagEntity baseTagEntity = new BaseTagEntity();
    BeanUtils.copyProperties(baseTagEntity, this);
    return baseTagEntity;
  }
}
