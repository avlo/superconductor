package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedPubkeyRepository;
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
public class FilterReferencedPubkeyPlugin<
    T extends SubscriberFilterReferencedPubkeyRepository<U>,
    U extends SubscriberFilterReferencedPubkey> implements FilterPlugin<T, U> {

  private final SubscriberFilterReferencedPubkeyRepository<U> join;

  @Autowired
  public FilterReferencedPubkeyPlugin(SubscriberFilterReferencedPubkeyRepository<U> join) {
    this.join = join;
  }

  @Override
  public void appendFilters(@NonNull Long filterId, @NonNull Filters filters) {
    filters.setReferencePubKeys(
        join.getAllByFilterId(filterId).stream().map(referencedPubkey ->
            new PublicKey(referencedPubkey.getReferencedPubkey())).toList());
  }

  @Override
  public List<U> getTypeSpecificFilterList(@NonNull Long filterId, @NonNull Filters filters) {
    return Optional.ofNullable(
            filters.getReferencedEvents())
        .orElseGet(ArrayList::new).stream().map(refPubkey ->
            (U) new SubscriberFilterReferencedPubkey(filterId, refPubkey.toString())).toList();
  }

  @Override
  public BiPredicate<PublicKey, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  }

  @Override
  public List<PublicKey> getPluginFilters(Filters filters) {
    return filters.getReferencePubKeys();
  }

  @Override
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "referencedPubkey";
  }
}
