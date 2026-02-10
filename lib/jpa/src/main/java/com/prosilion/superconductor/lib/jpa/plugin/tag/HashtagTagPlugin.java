package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.HashtagTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityHashtagTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.HashtagTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityHashtagTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.HashtagTagJpaEntityRepository;

@Component
public class HashtagTagPlugin<
    P extends HashtagTag,
    Q extends HashtagTagJpaEntityRepository<R>,
    R extends HashtagTagJpaEntity,
    S extends EventEntityHashtagTagJpaEntity,
    T extends EventEntityHashtagTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public HashtagTagPlugin(HashtagTagJpaEntityRepository<R> repo, EventEntityHashtagTagJpaEntityRepository<S> join) {
    super(repo, join, "t",
        tag -> (R) new HashtagTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityHashtagTagJpaEntity(eid, tid));
  }
}
