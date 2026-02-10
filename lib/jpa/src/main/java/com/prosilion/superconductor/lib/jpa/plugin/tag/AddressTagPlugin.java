package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.AddressTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityAddressTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.AddressTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityAddressTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.AddressTagJpaEntityRepository;

@Component
public class AddressTagPlugin<
    P extends AddressTag,
    Q extends AddressTagJpaEntityRepository<R>,
    R extends AddressTagJpaEntity,
    S extends EventEntityAddressTagJpaEntity,
    T extends EventEntityAddressTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public AddressTagPlugin(AddressTagJpaEntityRepository<R> repo, EventEntityAddressTagJpaEntityRepository<S> join) {
    super(repo, join, "a",
        tag -> (R) new AddressTagJpaEntity(tag),
        (eid, tid) -> (S) new EventEntityAddressTagJpaEntity(eid, tid));
  }
}
