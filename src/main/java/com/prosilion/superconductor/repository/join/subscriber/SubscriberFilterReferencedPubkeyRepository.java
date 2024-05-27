package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterReferencedPubkey;
import nostr.base.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberFilterReferencedPubkeyRepository extends JpaRepository<SubscriberFilterReferencedPubkey, Long> {
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, PublicKey referencedPubKey) {
    this.save(new SubscriberFilterReferencedPubkey(filterId, referencedPubKey.toString()));
  }
}