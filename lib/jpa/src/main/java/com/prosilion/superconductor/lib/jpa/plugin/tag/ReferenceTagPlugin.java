package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.ReferenceTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
  public ReferenceTagPlugin(ReferenceTagJpaEntityRepository<R> repo, EventEntityReferenceTagJpaEntityRepository<S> join) {
    super(repo, join, "r",
        tag -> (R) new ReferenceTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityReferenceTagJpaEntity(eid, tid));
  }
}
