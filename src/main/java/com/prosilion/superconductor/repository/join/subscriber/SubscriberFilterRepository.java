package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberFilterRepository extends JpaRepository<SubscriberFilter, Long> {
  Optional<List<SubscriberFilter>> findAllBySubscriberId(Long subscriberId);
  void deleteAllBySubscriberIdIn(List<Long> subscriberId);
}