package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterReferencedPubkey;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedPubkeyPlugin<T extends PublicKey> implements FilterPlugin<T> {

  @Override
  public BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  }

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return (List<T>) filters.getReferencePubKeys();
  }

  @Override
  public String getCode() {
    return "referencedPubkey";
  }
}
