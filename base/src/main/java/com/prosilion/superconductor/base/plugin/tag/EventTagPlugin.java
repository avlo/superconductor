package com.prosilion.superconductor.base.plugin.tag;

import com.prosilion.superconductor.base.dto.standard.EventTagDto;
import com.prosilion.superconductor.base.entity.join.standard.EventEntityEventTagEntity;
import com.prosilion.superconductor.base.entity.standard.EventTagEntity;
import com.prosilion.superconductor.base.repository.join.standard.EventEntityEventTagEntityRepository;
import com.prosilion.superconductor.base.repository.standard.EventTagEntityRepository;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventTagPlugin<
    P extends EventTag,
    Q extends EventTagEntityRepository<R>,
    R extends EventTagEntity,
    S extends EventEntityEventTagEntity,
    T extends EventEntityEventTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public EventTagPlugin(@NonNull EventTagEntityRepository<R> repo, @NonNull EventEntityEventTagEntityRepository<S> join) {
    super(repo, join, "e");
  }

  @Override
  public EventTagDto getTagDto(@NonNull P eventTag) {
    return new EventTagDto(eventTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long eventTagId) {
    return (S) new EventEntityEventTagEntity(eventId, eventTagId);
  }
}
