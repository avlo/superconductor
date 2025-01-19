package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.BaseTag;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

@Component
public class FilterAddressableTagPlugin<T extends Map<String, List<String>>> implements FilterPlugin<T> {

  @Override
  public BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (reqResult, genericEvent) ->

//      TODO: below return type currently won't work since AddressTag aren't formally part of clEvent (cont...)
//      List<AddressTag> genericEventAddressableTags = getAddressableTags(genericEventTags);
//      TODO, cont: so for now, use GenericTag- & when ready to replace below w/ AddressTag, see FilterIdentifierTagPlugin for usage pattern
        getAddressableTags(genericEvent.event().getTags()).stream()
            .map(GenericTag::getCode).toList().stream()
            .anyMatch(aTag -> reqResult.entrySet().stream().anyMatch(entry -> entry.getKey().equals("#".concat(aTag))));
  }

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return (List<T>) List.of(
        Optional.ofNullable(
                filters.getGenericTagQuery())
            .orElse(
                Collections.emptyMap()));
  }

  @Override
  public String getCode() {
    return "addressableTagQuery";
  }

  //      TODO: re: line 23- below method signature currently won't work since AddressTag aren't formally part of clEvent (cont...)
//  private List<AddressTag> getAddressableTags(List<BaseTag> baseTags) {
  private List<GenericTag> getAddressableTags(List<BaseTag> baseTags) {

//      TODO, cont: for now, use GenericTag (cont...)
    List<GenericTag> list = getGenericQueryTags(baseTags);

//      TODO, cont: use below filtering currently if/when AddressTag are formally part of clEvent
//    List<AddressTag> list = baseTags.stream()
//        .filter(AddressTag.class::isInstance)
//        .map(AddressTag.class::cast)
//        .toList();

    return list;
  }
}
