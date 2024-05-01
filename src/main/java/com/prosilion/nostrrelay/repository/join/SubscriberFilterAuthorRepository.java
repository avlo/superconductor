package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.SubscriberFilterAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberFilterAuthorRepository extends JpaRepository<SubscriberFilterAuthor, Long> {
  void deleteByFilterId(Long filterId);
}