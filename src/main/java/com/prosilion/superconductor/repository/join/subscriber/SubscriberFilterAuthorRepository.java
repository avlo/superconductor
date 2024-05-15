package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterAuthor;
import nostr.base.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubscriberFilterAuthorRepository extends JpaRepository<SubscriberFilterAuthor, Long> {
  void deleteByFilterId(Long filterId);

  default void save(Long filterId, PublicKey author) {
    this.save(new SubscriberFilterAuthor(filterId, author.toString()));
  }
}