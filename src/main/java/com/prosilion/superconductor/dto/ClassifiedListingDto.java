package com.prosilion.superconductor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prosilion.superconductor.entity.classified.ClassifiedListingEventEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.impl.ClassifiedListing;

@Setter
@Getter
public class ClassifiedListingDto extends ClassifiedListing {
  @JsonIgnore
  private final PriceTagDto priceTag;

  public ClassifiedListingDto(@NonNull String title, @NonNull String summary, @NonNull PriceTagDto priceTag) {
    super(title, summary, priceTag);
    this.priceTag = priceTag;
  }

  public ClassifiedListingEventEntity convertDtoToEntity() {
    return new ClassifiedListingEventEntity(getTitle(), getSummary(), getLocation(), getPublishedAt());
  }
}
