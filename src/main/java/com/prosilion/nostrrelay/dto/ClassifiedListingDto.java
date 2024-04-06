package com.prosilion.nostrrelay.dto;

import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;

@Setter
@Getter
public class ClassifiedListingDto extends ClassifiedListing {
  final PriceTagDto priceTag;

  public ClassifiedListingDto(String title, String summary, PriceTagDto priceTag) {
    super(title, summary, priceTag);
    this.priceTag = priceTag;
  }

  public ClassifiedListingEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    ClassifiedListingEntity classifiedListingEntity = new ClassifiedListingEntity();
    BeanUtils.copyProperties(classifiedListingEntity, this, "priceTag");
    return classifiedListingEntity;
  }
}
