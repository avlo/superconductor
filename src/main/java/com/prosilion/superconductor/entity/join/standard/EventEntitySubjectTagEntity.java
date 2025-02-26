package com.prosilion.superconductor.entity.join.standard;

import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-subject_tag-join")
public class EventEntitySubjectTagEntity extends EventEntityAbstractTagEntity {
  private Long subjectTagId;

  public EventEntitySubjectTagEntity(Long eventId, Long subjectTagId) {
    super.setEventId(eventId);
    this.subjectTagId = subjectTagId;
  }
}
