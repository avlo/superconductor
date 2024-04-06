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
@Table(name = "classified_listing-event-join")
public class ClassifiedListingEntityEventEntityJoin implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long classifiedListingId;

  public ClassifiedListingEntityEventEntityJoin(Long eventId, Long classifiedListingId) {
    this.eventId = eventId;
    this.classifiedListingId = classifiedListingId;
  }
}