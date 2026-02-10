package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
  public EventTagPlugin(EventTagJpaEntityRepository<R> repo, EventEntityEventTagJpaEntityRepository<S> join) {
    super(repo, join, "e",
        tag -> (R) new EventTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityEventTagJpaEntity(eid, tid));
  }
}
