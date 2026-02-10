package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.ReferenceTag;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.ConcreteTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityReferenceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.ReferenceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityReferenceTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.ReferenceTagJpaEntityRepository;

@Component
public class ReferenceTagPlugin<
    P extends ReferenceTag,
    Q extends ReferenceTagJpaEntityRepository<R>,
    R extends ReferenceTagJpaEntity,
    S extends EventEntityReferenceTagJpaEntity,
    T extends EventEntityReferenceTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public ReferenceTagPlugin(@Nonnull ReferenceTagJpaEntityRepository<R> repo, @NonNull EventEntityReferenceTagJpaEntityRepository<S> join) {
    super(repo, join, "r");
  }

  @Override
  public ConcreteTagDto getTagDto(@NonNull P referenceTag) {
    return new ConcreteTagDto<>(referenceTag, ReferenceTagJpaEntity::new);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long referenceTagId) {
    return (S) new EventEntityReferenceTagJpaEntity(eventId, referenceTagId);
  }
}
