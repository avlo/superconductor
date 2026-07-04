package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;

public interface AddressTagEventTagMappableIF {
  default List<BaseTag> cullATagETagMapFromBaseTags(@NonNull List<BaseTag> baseTags) {
    Map<AddressTag, EventTag> addressTagEventTagMap = new HashMap<>();
    List<BaseTag> filteredTags = new ArrayList<>(baseTags);

    ListIterator<BaseTag> iterator = filteredTags.listIterator();
    while (iterator.hasNext()) {
      BaseTag current = iterator.next();
      if (current instanceof AddressTag addressTag && iterator.hasNext()
         && filteredTags.get(iterator.nextIndex()) instanceof EventTag eventTag) {
        addressTagEventTagMap.put(addressTag, eventTag);
        iterator.remove();
        iterator.next();
        iterator.remove();
      }
    }

    setATagETagMap(addressTagEventTagMap);
    return filteredTags;
  }

  default List<BaseTag> getTags(List<BaseTag> tags) {
    List<BaseTag> collect = Stream.concat(
          tags.stream(),
          getATagETagMap().entrySet().stream()
             .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())))
       .collect(Collectors.toList());
    return collect;
  }

  Map<AddressTag, EventTag> getATagETagMap();
  void setATagETagMap(@NonNull Map<AddressTag, EventTag> eTagATagMap);
}
