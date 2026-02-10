package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.GeohashTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityGeohashTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.GeohashTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityGeohashTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.GeohashTagJpaEntityRepository;

@Component
public class GeohashTagPlugin<
    P extends GeohashTag,
    Q extends GeohashTagJpaEntityRepository<R>,
    R extends GeohashTagJpaEntity,
    S extends EventEntityGeohashTagJpaEntity,
    T extends EventEntityGeohashTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public GeohashTagPlugin(GeohashTagJpaEntityRepository<R> repo, EventEntityGeohashTagJpaEntityRepository<S> join) {
    super(repo, join, "g",
        tag -> (R) new GeohashTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityGeohashTagJpaEntity(eid, tid));
  }
}
