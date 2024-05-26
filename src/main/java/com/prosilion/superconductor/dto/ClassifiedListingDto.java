package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.classified.ClassifiedListingEventEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.impl.ClassifiedListing;

import java.util.Optional;

@Setter
@Getter
public class ClassifiedListingDto extends ClassifiedListing {
  final PriceTagDto priceTag;

  public ClassifiedListingDto(@NonNull String title, @NonNull String summary, @NonNull PriceTagDto priceTag) {
    super(title, summary, priceTag);
    this.priceTag = priceTag;
  }

  public ClassifiedListingEventEntity convertDtoToEntity() {
    ClassifiedListingEventEntity classifiedListingEventEntity = new ClassifiedListingEventEntity();
    classifiedListingEventEntity.setTitle(getTitle());
    classifiedListingEventEntity.setSummary(getSummary());
    Optional.ofNullable(getPublishedAt()).ifPresent(classifiedListingEventEntity::setPublishedAt);
    Optional.ofNullable(getLocation()).ifPresent(classifiedListingEventEntity::setLocation);
    return classifiedListingEventEntity;
  }
}
