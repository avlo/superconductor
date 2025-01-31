package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.plugin.filter.FilterIdentifierTagPlugin.IdentifierTagWrapper;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.BaseTag;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.IdentifierTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Component
public class FilterIdentifierTagPlugin<T extends IdentifierTagWrapper> implements FilterPlugin<T> {

  @Override
  public BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) ->
        getIdentifierTags(u.event().getTags()).stream()
            .map(IdentifierTag::getId)
            .anyMatch(dTag ->
                t.identifierTags().entrySet().stream().anyMatch(entry ->
                    entry.getValue().stream().anyMatch(value -> value.equals(dTag))));

  }

  @Override
  public Optional<List<T>> getPluginFilters(Filters filters) {
    return Optional.of((List<T>) List.of(new IdentifierTagWrapper(
        filters.getGenericTagQuery().entrySet().stream()
            .filter(mapEntry ->
                mapEntry.getKey().equals("#d"))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)))));
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

  record IdentifierTagWrapper(Map<String, List<String>> identifierTags) {
  }
}
