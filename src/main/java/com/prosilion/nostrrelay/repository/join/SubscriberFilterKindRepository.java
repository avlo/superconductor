package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.SubscriberFilterKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberFilterKindRepository extends JpaRepository<SubscriberFilterKind, Long> {
  void deleteByFilterId(Long filterId);
}