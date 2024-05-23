package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.classified.ClassifiedListingEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;

import java.util.Optional;

@Setter
@Getter
public class ClassifiedListingDto extends ClassifiedListing {
  final PriceTagDto priceTag;

  public ClassifiedListingDto(String title, String summary, PriceTagDto priceTag) {
    super(title, summary, priceTag);
    this.priceTag = priceTag;
  }

  public ClassifiedListingEntity convertDtoToEntity() {
    ClassifiedListingEntity classifiedListingEntity = new ClassifiedListingEntity();
    classifiedListingEntity.setTitle(getTitle());
    classifiedListingEntity.setSummary(getSummary());
    Optional.ofNullable(getPublishedAt()).ifPresent(classifiedListingEntity::setPublishedAt);
    Optional.ofNullable(getLocation()).ifPresent(classifiedListingEntity::setLocation);
    classifiedListingEntity.setPriceTags(priceTag);
    return classifiedListingEntity;
  }
}
