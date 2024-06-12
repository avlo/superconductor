package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.classified.ClassifiedListingDto;
import com.prosilion.superconductor.dto.classified.PriceTagDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
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
  private PriceTagDto priceTagDto;

  public ClassifiedListingEntity(@NonNull String title, @NonNull String summary, @NonNull String location, @NonNull PriceTagDto priceTagDto) {
    this.title = title;
    this.summary = summary;
    this.location = location;
    this.priceTagDto = priceTagDto;
  }

  public ClassifiedListingDto convertEntityToDto() {
    return new ClassifiedListingDto(title, summary, location, priceTagDto);
  }
}
