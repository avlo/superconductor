package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-subject_tag-join")
public class EventEntitySubjectTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntitySubjectTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
