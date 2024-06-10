package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.EventTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityEventTagEntity;
import com.prosilion.superconductor.entity.standard.EventTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityEventTagEntityRepository;
import com.prosilion.superconductor.repository.standard.EventTagEntityRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class EventTagModule<
    P extends EventTag,
    Q extends EventTagEntityRepository<R>,
    R extends EventTagEntity,
    S extends EventEntityEventTagEntity,
    U extends EventEntityEventTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  private final EventTagEntityRepository<R> eventTagEntityRepository;
  private final EventEntityEventTagEntityRepository<S> join;

  @Autowired
  public EventTagModule(@NonNull EventTagEntityRepository<R> eventTagEntityRepository, @NonNull EventEntityEventTagEntityRepository<S> join) {
    this.eventTagEntityRepository = eventTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "e";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) EventTagEntity.class;
  }

  @Override
  public R convertDtoToEntity(P eventTag) {
    return (R) getTagDto(eventTag).convertDtoToEntity();
  }

  @Override
  public EventTagDto getTagDto(P eventTag) {
    return new EventTagDto(eventTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long eventTagId) {
    return (S) new EventEntityEventTagEntity(eventId, eventTagId);
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
