package com.prosilion.nostrrelay.entity.join;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "classified_listing-tag-join")
public class ClassifiedListingEventTagEntityJoin implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long classifiedListingEventId;
  private Long classifiedListingId;

  public ClassifiedListingEventTagEntityJoin(Long classifiedListingEventId, Long classifiedListingId) {
    this.classifiedListingEventId = classifiedListingEventId;
    this.classifiedListingId = classifiedListingId;
  }
}