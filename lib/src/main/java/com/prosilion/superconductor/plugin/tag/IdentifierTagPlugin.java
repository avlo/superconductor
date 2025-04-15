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
    T extends EventEntityIdentifierTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public IdentifierTagPlugin(@NonNull IdentifierTagEntityRepository<R> repo, @NonNull EventEntityIdentifierTagEntityRepository<S> join) {
    super(repo, join, "d");
  }

  @Override
  public IdentifierTagDto getTagDto(P identifierTag) {
    return new IdentifierTagDto(identifierTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long identifierTagId) {
    return (S) new EventEntityIdentifierTagEntity(eventId, identifierTagId);
  }
}
