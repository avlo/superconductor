package com.prosilion.nostrrelay.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.tag.PriceTag;
import org.springframework.beans.BeanUtils;

import java.util.List;
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
    List<PriceTag> priceTags = List.of(new PriceTag("666", "number", "BTC", "frequency"));
    ClassifiedListing classifiedListingDto = new ClassifiedListing(title, summary, priceTags);
    BeanUtils.copyProperties(classifiedListingDto, this);
    return classifiedListingDto;
  }
}
