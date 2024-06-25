package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterAuthorRepository;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class FilterAuthorPlugin implements FilterPlugin {

  private final SubscriberFilterAuthorRepository<SubscriberFilterAuthor> join;

  @Autowired
  public FilterAuthorPlugin(SubscriberFilterAuthorRepository<SubscriberFilterAuthor> subscriberFilterAuthorRepository) {
    this.join = subscriberFilterAuthorRepository;
  }

  @Override
  public void appendFilters(Long filterId, Filters filters) {
    filters.setAuthors(
        join.getAllByFilterId(filterId).stream().map(author ->
            new PublicKey(author.getAuthor())).toList());
  }

  @Override
  public void saveFilter(Long filterId, Filters filters) {
    join.saveAllAndFlush(() ->
        Optional.ofNullable(
                filters.getAuthors())
            .orElseGet(ArrayList::new).stream().map(authorPubkey ->
                new SubscriberFilterAuthor(filterId, authorPubkey.toString())).toList().iterator());
  }

  @Override
  public SubscriberFilterAuthorRepository getJoin() {
    return join;
  }

  @Override
  public String getCode() {
    return "author";
  }
}
