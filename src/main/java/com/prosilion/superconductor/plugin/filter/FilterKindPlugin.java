package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterKind;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterKindPlugin<T extends SubscriberFilterKind> implements FilterPlugin<T> {

  @Override
  public BiPredicate<PublicKey, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  }

  @Override
  public List<Kind> getPluginFilters(Filters filters) {
    return filters.getKinds();
  }

  @Override
  public String getCode() {
    return "kind";
  }
}
