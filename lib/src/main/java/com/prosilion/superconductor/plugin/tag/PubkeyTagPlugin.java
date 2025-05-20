package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntity;
import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityPubkeyTagEntityRepository;
import com.prosilion.superconductor.repository.standard.PubkeyTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PubkeyTagPlugin<
    P extends PubKeyTag,
    Q extends PubkeyTagEntityRepository<R>,
    R extends PubkeyTagEntity,
    S extends EventEntityPubkeyTagEntity,
    T extends EventEntityPubkeyTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public PubkeyTagPlugin(@Nonnull PubkeyTagEntityRepository<R> repo, @NonNull EventEntityPubkeyTagEntityRepository<S> join) {
    super(repo, join, "p");
  }

  @Override
  public PubkeyTagDto getTagDto(@NonNull P pubkeyTag) {
    return new PubkeyTagDto(pubkeyTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long pubkeyId) {
    return (S) new EventEntityPubkeyTagEntity(eventId, pubkeyId);
  }
}
