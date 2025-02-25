package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityIdentifierTagEntity;
import com.prosilion.superconductor.entity.standard.IdentifierTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityIdentifierTagEntityRepository;
import com.prosilion.superconductor.repository.standard.IdentifierTagEntityRepository;
import lombok.NonNull;
import nostr.event.tag.IdentifierTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentifierTagPlugin<
    P extends IdentifierTag,
    Q extends IdentifierTagEntityRepository<R>,
    R extends IdentifierTagEntity,
    S extends EventEntityIdentifierTagEntity,
    T extends EventEntityIdentifierTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, T> {

  private final IdentifierTagEntityRepository<R> identifierTagEntityRepository;
  private final EventEntityIdentifierTagEntityRepository<S> join;

  @Autowired
  public IdentifierTagPlugin(@NonNull IdentifierTagEntityRepository<R> identifierTagEntityRepository, @NonNull EventEntityIdentifierTagEntityRepository<S> join) {
    this.identifierTagEntityRepository = identifierTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "d";
  }

  @Override
  public R convertDtoToEntity(P identifierTag) {
    return (R) getTagDto(identifierTag).convertDtoToEntity();
  }

  @Override
  public IdentifierTagDto getTagDto(P identifierTag) {
    return new IdentifierTagDto(identifierTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long identifierTagId) {
    return (S) new EventEntityIdentifierTagEntity(eventId, identifierTagId);
  }

  @Override
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepository() {
    return (Q) identifierTagEntityRepository;
  }
}
