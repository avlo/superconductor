package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedPubkeyRepository;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class FilterReferencedPubkeyPlugin implements FilterPlugin {

  private final SubscriberFilterReferencedPubkeyRepository<SubscriberFilterReferencedPubkey> join;

  @Autowired
  public FilterReferencedPubkeyPlugin(SubscriberFilterReferencedPubkeyRepository<SubscriberFilterReferencedPubkey> join) {
    this.join = join;
  }

  @Override
  public Filters appendFilters(Long filterId, Filters filters) {
    filters.setReferencePubKeys(
        join.getAllByFilterId(filterId).stream().map(referencedPubkey ->
            new PublicKey(referencedPubkey.getReferencedPubkey())).toList());
    return filters;
  }

  @Override
  public void saveFilter(Long filterId, Filters filters) {
// TODO: saveAllAndFlush() vs save(), possibly solves inconsistency issues w/ entityManager?
    join.saveAllAndFlush(() ->
        Optional.ofNullable(
                filters.getReferencedEvents())
            .orElseGet(ArrayList::new).stream().map(refPubkey ->
                new SubscriberFilterReferencedPubkey(filterId, refPubkey.toString())).toList().iterator());
  }

  @Override
  public SubscriberFilterReferencedPubkeyRepository getJoin() {
    return join;
  }

  @Override
  public String getCode() {
    return "referencedPubkey";
  }
}
