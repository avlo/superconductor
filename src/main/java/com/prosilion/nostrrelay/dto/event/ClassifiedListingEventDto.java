package com.prosilion.nostrrelay.dto;

import com.prosilion.nostrrelay.entity.ClassifiedListingEventEntity;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.ClassifiedListingEvent;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
public class ClassifiedListingEventDto extends ClassifiedListingEvent {
  public ClassifiedListingEventDto(PublicKey sender, List<BaseTag> baseTags, String content, ClassifiedListing classifiedListing) {
    super(sender, baseTags, content, classifiedListing);
  }

  public ClassifiedListingEventEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    ClassifiedListingEventEntity classifiedListingEventEntity = new ClassifiedListingEventEntity();
    BeanUtils.copyProperties(classifiedListingEventEntity, this);
    BeanUtils.copyProperty(classifiedListingEventEntity, "eventId", this.getId());
    return classifiedListingEventEntity;
  }
}
