package com.prosilion.superconductor.entity.join.generic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-generic_tag-join")
public class EventEntityGenericTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long eventId;
  private Long genericTagId;

  public EventEntityGenericTagEntity(Long eventId, Long genericTagId) {
    this.eventId = eventId;
    this.genericTagId = genericTagId;
  }
}