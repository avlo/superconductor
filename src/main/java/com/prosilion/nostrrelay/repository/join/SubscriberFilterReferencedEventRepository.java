package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.SubscriberFilterKind;
import com.prosilion.nostrrelay.entity.join.SubscriberFilterReferencedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SubscriberFilterReferencedEventRepository extends JpaRepository<SubscriberFilterReferencedEvent, Long> {
}