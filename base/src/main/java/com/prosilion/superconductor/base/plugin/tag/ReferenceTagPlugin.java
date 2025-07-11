package com.prosilion.superconductor.base.plugin.tag;

import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.base.dto.standard.ReferenceTagDto;
import com.prosilion.superconductor.base.entity.join.standard.EventEntityReferenceTagEntity;
import com.prosilion.superconductor.base.entity.standard.ReferenceTagEntity;
import com.prosilion.superconductor.base.repository.join.standard.EventEntityReferenceTagEntityRepository;
import com.prosilion.superconductor.base.repository.standard.ReferenceTagEntityRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
