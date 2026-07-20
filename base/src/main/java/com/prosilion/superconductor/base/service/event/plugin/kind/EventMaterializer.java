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
    return materializeStream(genericEventRecords.stream());
  }

  default Stream<T> materializeStream(Stream<GenericEventRecord> genericEventRecords) {
    return genericEventRecords
       .mapMulti((genericEventRecord, consumer) ->
          materialize(genericEventRecord).ifPresent(consumer));
  }

  default Optional<T> materializeFirst(List<GenericEventRecord> genericEventRecords) {
    return materializeFirst(genericEventRecords.stream());
  }

  default Optional<T> materializeFirst(Stream<GenericEventRecord> genericEventRecords) {
    return genericEventRecords.findFirst().flatMap(this::materialize);
//    TODO: optionally, return materializeStream(genericEventRecords).findFirst();
  }
}
