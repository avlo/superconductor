package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.SubscriberFilterReferencedEvent;
import com.prosilion.nostrrelay.entity.join.SubscriberFilterReferencedPubkey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SubscriberFilterReferencedPubkeyRepository extends JpaRepository<SubscriberFilterReferencedPubkey, Long> {
}