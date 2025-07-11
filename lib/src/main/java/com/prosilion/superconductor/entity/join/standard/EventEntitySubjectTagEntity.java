package com.prosilion.superconductor.entity.join.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("event-subject_tag-join")
public class EventEntitySubjectTagEntity extends EventEntityAbstractEntity {
  private Long subjectTagId;

  public EventEntitySubjectTagEntity(Long eventId, Long subjectTagId) {
    super.setEventId(eventId);
    this.subjectTagId = subjectTagId;
  }
}
