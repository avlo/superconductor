package com.prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityPubkeyTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.PubkeyTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityPubkeyTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.PubkeyTagJpaEntityRepository;

@Component
public class PubkeyTagPlugin<
    P extends PubKeyTag,
    Q extends PubkeyTagJpaEntityRepository<R>,
    R extends PubkeyTagJpaEntity,
    S extends EventEntityPubkeyTagJpaEntity,
    T extends EventEntityPubkeyTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public PubkeyTagPlugin(@Nonnull PubkeyTagJpaEntityRepository<R> repo, @NonNull EventEntityPubkeyTagJpaEntityRepository<S> join) {
    super(repo, join, "p");
  }

  @Override
  public PubkeyTagDto getTagDto(@NonNull P pubkeyTag) {
    return new PubkeyTagDto(pubkeyTag);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long pubkeyId) {
    return (S) new EventEntityPubkeyTagJpaEntity(eventId, pubkeyId);
  }
}
