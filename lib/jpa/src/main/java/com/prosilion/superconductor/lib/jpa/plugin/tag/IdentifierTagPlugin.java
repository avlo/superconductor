package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.IdentifierTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityIdentifierTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.IdentifierTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityIdentifierTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.IdentifierTagEntityRepository;

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
  public IdentifierTagDto getTagDto(@NonNull P identifierTag) {
    return new IdentifierTagDto(identifierTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long identifierTagId) {
    return (S) new EventEntityIdentifierTagEntity(eventId, identifierTagId);
  }
}
