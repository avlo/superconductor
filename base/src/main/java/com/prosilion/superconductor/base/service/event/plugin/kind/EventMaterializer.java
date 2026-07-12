package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;

@FunctionalInterface
public interface EventMaterializer<T extends BaseEvent> {
  Optional<T> materialize(@NonNull EventIF eventIF);

  default Stream<T> materializeList(List<GenericEventRecord> genericEventRecords) {
    return genericEventRecords.stream()
       .mapMulti((genericEventRecord, consumer) ->
          materialize(genericEventRecord).ifPresent(consumer));
  }

  default Optional<T> materializeFirst(List<GenericEventRecord> genericEventRecords) {
    return genericEventRecords.stream().findFirst().flatMap(this::materialize);
  }
}
