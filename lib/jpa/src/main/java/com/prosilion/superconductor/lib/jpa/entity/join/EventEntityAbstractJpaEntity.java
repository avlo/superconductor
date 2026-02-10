package com.prosilion.superconductor.lib.jpa.entity.join;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class EventEntityAbstractJpaEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long eventId;
  private Long tagId;

  public EventEntityAbstractJpaEntity(Long eventId) {
    this.eventId = eventId;
  }

  public EventEntityAbstractJpaEntity(Long eventId, Long tagId) {
    this.eventId = eventId;
    this.tagId = tagId;
  }
}
