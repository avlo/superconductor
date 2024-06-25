package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterReferencedPubkey;
import nostr.base.PublicKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberFilterReferencedPubkeyRepository<T extends SubscriberFilterReferencedPubkey> extends AbstractSubscriberFilterTypeJoinRepository<T> {
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, PublicKey referencedPubKey) {
    save((T) new SubscriberFilterReferencedPubkey(filterId, referencedPubKey.toString()));
  }
}