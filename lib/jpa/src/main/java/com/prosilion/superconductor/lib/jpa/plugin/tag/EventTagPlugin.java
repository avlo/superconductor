package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.EventTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityEventTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.EventTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityEventTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.EventTagJpaEntityRepository;

@Component
public class EventTagPlugin<
    P extends EventTag,
    Q extends EventTagJpaEntityRepository<R>,
    R extends EventTagJpaEntity,
    S extends EventEntityEventTagJpaEntity,
    T extends EventEntityEventTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public EventTagPlugin(@NonNull EventTagJpaEntityRepository<R> repo, @NonNull EventEntityEventTagJpaEntityRepository<S> join) {
    super(repo, join, "e");
  }

  @Override
  public EventTagDto getTagDto(@NonNull P eventTag) {
    return new EventTagDto(eventTag);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long eventTagId) {
    return (S) new EventEntityEventTagJpaEntity(eventId, eventTagId);
  }
}
