package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.IdentifierTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityIdentifierTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.IdentifierTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityIdentifierTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.IdentifierTagJpaEntityRepository;

@Component
public class IdentifierTagPlugin<
    P extends IdentifierTag,
    Q extends IdentifierTagJpaEntityRepository<R>,
    R extends IdentifierTagJpaEntity,
    S extends EventEntityIdentifierTagJpaEntity,
    T extends EventEntityIdentifierTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public IdentifierTagPlugin(IdentifierTagJpaEntityRepository<R> repo, EventEntityIdentifierTagJpaEntityRepository<S> join) {
    super(repo, join, "d",
        tag -> (R) new IdentifierTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityIdentifierTagJpaEntity(eid, tid));
  }
}
