package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.SubscriberFilterEventJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SubscriberFilterEventRepositoryJoin extends JpaRepository<SubscriberFilterEventJoin, Long> {
}