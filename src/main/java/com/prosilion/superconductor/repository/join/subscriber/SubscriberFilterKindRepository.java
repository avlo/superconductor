package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberFilterKindRepository extends JpaRepository<SubscriberFilterKind, Long> {
  void deleteByFilterId(Long filterId);

  default void save(Long filterId, Integer kind) {
    this.save(new SubscriberFilterKind(filterId, kind));
  }
}