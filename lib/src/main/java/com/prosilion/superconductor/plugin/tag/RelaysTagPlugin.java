package com.prosilion.superconductor.plugin.tag;

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
    T extends EventEntityRelaysTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public RelaysTagPlugin(@Nonnull RelaysTagEntityRepository<R> repo, @NonNull EventEntityRelaysTagEntityRepository<S> join) {
    super(repo, join, "relays");
  }

  @Override
  public RelaysTagDto getTagDto(@NonNull P relaysTag) {
    return new RelaysTagDto(relaysTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long relaysTagId) {
    return (S) new EventEntityRelaysTagEntity(eventId, relaysTagId);
  }
}
