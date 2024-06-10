package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.classified.ClassifiedListingDto;
import com.prosilion.superconductor.dto.classified.PriceTagDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.impl.ClassifiedListing;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "classified_listing")
public class ClassifiedListingEventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String summary;
  private String location;

  @Column(name = "published_at")
  private Long publishedAt;

  public ClassifiedListingEventEntity(@NonNull ClassifiedListing classifiedListing) {
    this.title = classifiedListing.getTitle();
    this.summary = classifiedListing.getSummary();
    this.location = classifiedListing.getLocation();
    this.publishedAt = classifiedListing.getPublishedAt();
  }

  public ClassifiedListingDto convertEntityToDto(PriceTagDto priceTagDto) {
    return new ClassifiedListingDto(title, summary, priceTagDto);
  }
}
