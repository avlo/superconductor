package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.BaseTag;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.IdentifierTag;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

@Component
public class FilterIdentifierTagPlugin<T extends Map<String, List<String>>> implements FilterPlugin<T> {

  @Override
  public BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) ->
    {
      return getIdentifierTags(u.event().getTags()).stream()
          .map(IdentifierTag::getId)
          .anyMatch(dTag -> t.entrySet().stream().anyMatch(entry -> entry.getValue().getFirst().equals(dTag)));
    };
  }

  @Override
  public List<T> getPluginFilters(Filters filters) {
    Map<String, List<String>> genericTagQuery = filters.getGenericTagQuery();
    return (List<T>) List.of(
        Optional.ofNullable(
                genericTagQuery)
            .orElse(
                Collections.emptyMap()));
  }

  @Override
  public String getCode() {
    return "identifierTagQuery";
  }

  private List<IdentifierTag> getIdentifierTags(List<BaseTag> baseTags) {
    return baseTags.stream()
        .filter(IdentifierTag.class::isInstance)
        .map(IdentifierTag.class::cast)
        .toList();
  }
}
