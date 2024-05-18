package com.prosilion.superconductor.entity.join;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-subject_tag-join")
public class EventEntitySubjectTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long subjectTagId;

  public EventEntitySubjectTagEntity(Long eventId, Long subjectTagId) {
    this.eventId = eventId;
    this.subjectTagId = subjectTagId;
  }
}