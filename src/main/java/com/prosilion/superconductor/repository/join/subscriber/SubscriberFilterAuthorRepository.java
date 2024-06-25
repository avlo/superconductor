package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterAuthor;
import nostr.base.PublicKey;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubscriberFilterAuthorRepository<T extends SubscriberFilterAuthor> extends AbstractSubscriberFilterTypeJoinRepository<T> {
  void deleteAllByFilterId(Long filterId);
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, PublicKey author) {
    save((T)new SubscriberFilterAuthor(filterId, author.toString()));
  }
}