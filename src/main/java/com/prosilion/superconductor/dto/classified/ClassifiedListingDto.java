package com.prosilion.superconductor.dto.classified;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prosilion.superconductor.entity.classified.ClassifiedListingEventEntity;
import lombok.NonNull;
import nostr.event.impl.ClassifiedListing;

public class ClassifiedListingDto extends ClassifiedListing {
  @JsonIgnore
  private final PriceTagDto priceTag;

  public ClassifiedListingDto(@NonNull String title, @NonNull String summary, @NonNull PriceTagDto priceTagDto) {
    super(title, summary, priceTagDto.getPriceTag());
    this.priceTag = priceTagDto;
  }

  public ClassifiedListingEventEntity convertDtoToEntity() {
    return new ClassifiedListingEventEntity(this);
  }
}
