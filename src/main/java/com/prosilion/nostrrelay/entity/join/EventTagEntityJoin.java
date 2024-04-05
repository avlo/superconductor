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
@Table(name = "event-base_tag-join")
public class EventTagEntityJoin implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long baseTagId;

  public EventTagEntityJoin(Long eventId, Long baseTagId) {
    this.eventId = eventId;
    this.baseTagId = baseTagId;
  }
}