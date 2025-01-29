package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.Kind;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterKindPlugin<T extends Kind> implements FilterPlugin<T> {

  @Override
  public BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.equals(Kind.valueOf(u.event().getKind()));
  }

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return (List<T>) filters.getKinds();
  }

  @Override
  public String getCode() {
    return "kind";
  }
}
