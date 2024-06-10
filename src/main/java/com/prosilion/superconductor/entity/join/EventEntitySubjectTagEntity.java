package com.prosilion.superconductor.entity.join;

import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-subject_tag-join")
public class EventEntitySubjectTagEntity extends EventEntityStandardTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long subjectTagId;

  public EventEntitySubjectTagEntity(Long eventId, Long subjectTagId) {
    this.eventId = eventId;
    this.subjectTagId = subjectTagId;
  }

  @Override
  public Long getLookupId() {
    return subjectTagId;
  }
}