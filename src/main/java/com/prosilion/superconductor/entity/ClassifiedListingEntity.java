package com.prosilion.superconductor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.tag.PriceTag;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "classified_listing")
public class ClassifiedListingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String summary;
  private String location;

  @Column(name = "published_at")
  private Long publishedAt;

  public ClassifiedListingEntity(String title, String summary, String location, Long publishedAt) {
    this.title = title;
    this.summary = summary;
    this.location = location;
    this.publishedAt = publishedAt;
  }

  public ClassifiedListing convertEntityToDto() {
    // TODO: below
    PriceTag priceTags = new PriceTag(new BigDecimal(666), "BTC", "frequency");
    return new ClassifiedListing(title, summary, priceTags);
  }
}
