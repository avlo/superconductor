package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterAuthorRepository;
import lombok.NonNull;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@Component
public class FilterAuthorPlugin<
    T extends SubscriberFilterAuthorRepository<U>,
    U extends SubscriberFilterAuthor> implements FilterPlugin<T, U> {

  private final SubscriberFilterAuthorRepository<U> join;

  @Autowired
  public FilterAuthorPlugin(SubscriberFilterAuthorRepository<U> subscriberFilterAuthorRepository) {
    this.join = subscriberFilterAuthorRepository;
  }

  @Override
  public void appendFilters(@NonNull Long filterId, @NonNull Filters filters) {
    filters.setAuthors(
        join.getAllByFilterId(filterId).stream().map(author ->
            new PublicKey(author.getAuthor())).toList());
  }

  @Override
  public List<U> getTypeSpecificFilterList(@NonNull Long filterId, @NonNull Filters filters) {
    return Optional.ofNullable(
            filters.getAuthors())
        .orElseGet(ArrayList::new).stream().map(authorPubkey ->
            (U) new SubscriberFilterAuthor(filterId, authorPubkey.toString())).toList();
  }

  @Override
  public BiPredicate<?, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  }

  @Override
  public List<PublicKey> getPluginFilters(Filters filters) {
    return filters.getAuthors();
  }

  @Override
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "author";
  }
}
