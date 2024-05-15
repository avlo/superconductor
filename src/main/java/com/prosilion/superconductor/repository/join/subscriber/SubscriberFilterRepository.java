package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberFilterRepository extends JpaRepository<SubscriberFilter, Long> {
  Optional<SubscriberFilter> findBySubscriberId(Long subscriberId);
}