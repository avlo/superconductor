package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.SubscriberFilterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SubscriberFilterEventRepository extends JpaRepository<SubscriberFilterEvent, Long> {
}