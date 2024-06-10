package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.EventTagDto;
import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.join.standard.EventEntityEventTagEntity;
import com.prosilion.superconductor.entity.standard.EventTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityEventTagEntityRepository;
import com.prosilion.superconductor.repository.standard.EventTagEntityRepository;
import nostr.event.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventTagModule<
    P extends EventTag,
    Q extends EventTagEntityRepository<R>,
    R extends EventTagEntity,
    S extends EventEntityEventTagEntity,
    U extends EventEntityEventTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  EventTagEntityRepository<R> eventTagEntityRepository;
  EventEntityEventTagEntityRepository<S> join;
  private P eventTag;

  @Autowired
  public EventTagModule(EventTagEntityRepository<R> eventTagEntityRepository, EventEntityEventTagEntityRepository<S> join) {
    this.eventTagEntityRepository = eventTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "subject";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) EventTagEntity.class;
  }

  @Override
  public void setBaseTag(P eventTag) {
    this.eventTag = eventTag;
  }

  @Override
  public R convertDtoToEntity() {
    return getTagDto().convertDtoToEntity();
  }

  @Override
  public StandardTagDtoRxR getTagDto() {
    return new EventTagDto(eventTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long subjectTagId) {
    return (S) new EventEntityEventTagEntity(eventId, subjectTagId);
  }

  @Override
  public U getEventEntityStandardTagEntityRepositoryJoin() {
    return (U) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) eventTagEntityRepository;
  }
}
