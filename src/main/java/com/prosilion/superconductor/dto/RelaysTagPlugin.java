package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityRelaysTagEntity;
import com.prosilion.superconductor.entity.standard.RelaysTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityRelaysTagEntityRepository;
import com.prosilion.superconductor.repository.standard.RelaysTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import nostr.event.tag.RelaysTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelaysTagPlugin<
    P extends RelaysTag,
    Q extends RelaysTagEntityRepository<R>,
    R extends RelaysTagEntity,
    S extends EventEntityRelaysTagEntity,
    T extends EventEntityRelaysTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, T> {

  private final RelaysTagEntityRepository<R> relaysTagEntityRepository;
  private final EventEntityRelaysTagEntityRepository<S> join;

  @Autowired
  public RelaysTagPlugin(@Nonnull RelaysTagEntityRepository<R> relaysTagEntityRepository, @NonNull EventEntityRelaysTagEntityRepository<S> join) {
    this.relaysTagEntityRepository = relaysTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "relays";
  }

  @Override
  public R convertDtoToEntity(P relaysTag) {
    return (R) getTagDto(relaysTag).convertDtoToEntity();
  }

  @Override
  public RelaysTagDto getTagDto(P relaysTag) {
    return new RelaysTagDto(relaysTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long relaysTagId) {
    return (S) new EventEntityRelaysTagEntity(eventId, relaysTagId);
  }

  @Override
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) relaysTagEntityRepository;
  }
}
