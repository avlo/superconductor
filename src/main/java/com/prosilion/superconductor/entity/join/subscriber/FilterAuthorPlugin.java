package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterAuthorRepository;
import lombok.NonNull;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "author";
  }
}
