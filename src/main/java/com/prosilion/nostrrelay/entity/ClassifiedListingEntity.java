package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.ClassifiedListingDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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

  //  private List<PriceTag> priceTags;

  public ClassifiedListingEntity(String title, String summary, String location, Long publishedAt) {
    this.title = title;
    this.summary = summary;
    this.location = location;
    this.publishedAt = publishedAt;
  }

  public ClassifiedListingDto convertEntityToDto() {
    List<PriceTag> priceTags = List.of(new PriceTag("666", "number", "BTC", "frequency"));
    ClassifiedListingDto classifiedListingDto = new ClassifiedListingDto(title, summary, priceTags);
    BeanUtils.copyProperties(classifiedListingDto, this);
    return classifiedListingDto;
  }
}
