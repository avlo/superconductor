package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.lib.jpa.dto.standard.ExternalIdentityTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityExternalIdentityTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.ExternalIdentityTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityExternalIdentityTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.ExternalIdentityTagJpaEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ExternalIdentityTagPlugin<
    P extends ExternalIdentityTag,
    Q extends ExternalIdentityTagJpaEntityRepository<R>,
    R extends ExternalIdentityTagJpaEntity,
    S extends EventEntityExternalIdentityTagJpaEntity,
    T extends EventEntityExternalIdentityTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public ExternalIdentityTagPlugin(@NonNull ExternalIdentityTagJpaEntityRepository<R> repo, @NonNull EventEntityExternalIdentityTagJpaEntityRepository<S> join) {
    super(repo, join, "i");
  }

  @Override
  public ExternalIdentityTagDto getTagDto(@NonNull P externalIdentityTag) {
    return new ExternalIdentityTagDto(externalIdentityTag);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long externalIdentityTagId) {
    return (S) new EventEntityExternalIdentityTagJpaEntity(eventId, externalIdentityTagId);
  }
}
