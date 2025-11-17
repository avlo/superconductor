package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public interface CacheEventTagBaseEventServiceIF {
  void save(EventIF event);

  List<? extends EventTagsMappedEventsIF> getAll();
  List<? extends EventTagsMappedEventsIF> getByKind(Kind kind);
  Optional<? extends EventTagsMappedEventsIF> getEvent(@NonNull EventIF eventIF);
  Optional<? extends EventTagsMappedEventsIF> getEventByEventId(String eventId);
  <T extends EventIF> void deleteEvent(T event);
  <T extends BaseEvent> T createEventGivenMappedEventTagEvents(
      GenericEventRecord eventIF,
      Class<T> eventClassFromKind,
      Function<EventTag, ? extends BaseEvent> fxn);

  @SneakyThrows
  default <T extends BaseEvent> T createBaseEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind,
      @NonNull Function<EventTag, ? extends BaseEvent> exampleFunction) {
    assertNotNull(baseEventFromKind);
    Constructor<T> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class, Function.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return constructor.newInstance(genericEventRecord, exampleFunction);
  }

  Kind getKind();
}
