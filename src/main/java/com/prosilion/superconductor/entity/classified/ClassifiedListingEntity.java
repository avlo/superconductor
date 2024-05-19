package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.ClassifiedListingDto;
import com.prosilion.superconductor.dto.PriceTagDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Transient
  private PriceTagDto priceTags;

  public ClassifiedListingEntity(String title, String summary, String location, Long publishedAt, PriceTagDto priceTags) {
    this.title = title;
    this.summary = summary;
    this.location = location;
    this.publishedAt = publishedAt;
    this.priceTags = priceTags;
  }

  public ClassifiedListingDto convertEntityToDto() {
    return new ClassifiedListingDto(title, summary, priceTags);
  }
}
