package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
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
public class EventEntitySubjectTagEntity extends EventEntityAbstractEntity {
  private Long subjectTagId;

  public EventEntitySubjectTagEntity(Long eventId, Long subjectTagId) {
    super.setEventId(eventId);
    this.subjectTagId = subjectTagId;
  }
}
