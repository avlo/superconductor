package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
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
public class EventEntitySubjectTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long subjectTagId;

  public EventEntitySubjectTagJpaEntity(Long eventId, Long subjectTagId) {
    super.setEventId(eventId);
    this.subjectTagId = subjectTagId;
  }
}
