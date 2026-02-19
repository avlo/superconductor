package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityRelayTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.RelayTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityRelayTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.RelayTagJpaEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelayTagPlugin<
    P extends RelayTag,
    Q extends RelayTagJpaEntityRepository<R>,
    R extends RelayTagJpaEntity,
    S extends EventEntityRelayTagJpaEntity,
    T extends EventEntityRelayTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public RelayTagPlugin(RelayTagJpaEntityRepository<R> repo, EventEntityRelayTagJpaEntityRepository<S> join) {
    super(repo, join, "relay",
        tag -> (R) new RelayTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityRelayTagJpaEntity(eid, tid));
  }
}
