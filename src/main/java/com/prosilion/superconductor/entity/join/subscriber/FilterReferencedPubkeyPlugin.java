package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedPubkeyPlugin<T extends SubscriberFilterReferencedPubkey> implements FilterPlugin<T> {

  @Override
  public BiPredicate<PublicKey, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  }

  @Override
  public List<PublicKey> getPluginFilters(Filters filters) {
    return filters.getReferencePubKeys();
  }

  @Override
  public String getCode() {
    return "referencedPubkey";
  }
}
