package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.ReferenceTag;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.ReferenceTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityReferenceTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.ReferenceTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityReferenceTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.ReferenceTagEntityRepository;

@Component
public class ReferenceTagPlugin<
    P extends ReferenceTag,
    Q extends ReferenceTagEntityRepository<R>,
    R extends ReferenceTagEntity,
    S extends EventEntityReferenceTagEntity,
    T extends EventEntityReferenceTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public ReferenceTagPlugin(@Nonnull ReferenceTagEntityRepository<R> repo, @NonNull EventEntityReferenceTagEntityRepository<S> join) {
    super(repo, join, "r");
  }

  @Override
  public ReferenceTagDto getTagDto(@NonNull P referenceTag) {
    return new ReferenceTagDto(referenceTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long referenceTagId) {
    return (S) new EventEntityReferenceTagEntity(eventId, referenceTagId);
  }
}
