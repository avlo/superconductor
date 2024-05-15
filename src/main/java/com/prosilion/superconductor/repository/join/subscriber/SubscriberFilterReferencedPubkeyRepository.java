package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterReferencedPubkey;
import nostr.base.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberFilterReferencedPubkeyRepository extends JpaRepository<SubscriberFilterReferencedPubkey, Long> {
  void deleteByFilterId(Long filterId);

  default void save(Long filterId, PublicKey referencedPubKey) {
    this.save(new SubscriberFilterReferencedPubkey(filterId, referencedPubKey.toString()));
  }
}