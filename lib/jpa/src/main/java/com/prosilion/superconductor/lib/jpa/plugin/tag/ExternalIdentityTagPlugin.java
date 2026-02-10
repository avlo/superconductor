package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.ExternalIdentityTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityExternalIdentityTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.ExternalIdentityTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityExternalIdentityTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.ExternalIdentityTagJpaEntityRepository;

@Component
public class ExternalIdentityTagPlugin<
    P extends ExternalIdentityTag,
    Q extends ExternalIdentityTagJpaEntityRepository<R>,
    R extends ExternalIdentityTagJpaEntity,
    S extends EventEntityExternalIdentityTagJpaEntity,
    T extends EventEntityExternalIdentityTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public ExternalIdentityTagPlugin(ExternalIdentityTagJpaEntityRepository<R> repo, EventEntityExternalIdentityTagJpaEntityRepository<S> join) {
    super(repo, join, "i",
        tag -> (R) new ExternalIdentityTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityExternalIdentityTagJpaEntity(eid, tid));
  }
}
