package com.prosilion.superconductor.dto.classified;

import com.prosilion.superconductor.entity.classified.ClassifiedListingEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.impl.ClassifiedListing;

@Setter
@Getter
public class ClassifiedListingDto extends ClassifiedListing {
  private final String title;
  private final String summary;
  private final String location;
  private Long publishedAt;

  public ClassifiedListingDto(@NonNull String title, @NonNull String summary, @NonNull String location, @NonNull PriceTagDto priceTagDto) {
    super(title, summary, priceTagDto.getPriceTag());
    this.title = title;
    this.summary = summary;
    this.location = location;
  }

  public ClassifiedListingEntity convertDtoToEntity() {
    return new ClassifiedListingEntity(title, summary, location, new PriceTagDto(getPriceTag()));
  }
}
