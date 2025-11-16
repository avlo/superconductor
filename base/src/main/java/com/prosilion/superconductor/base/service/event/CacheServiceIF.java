package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public interface CacheServiceIF {
  GenericEventRecord save(EventIF event);
  List<GenericEventRecord> getAll();
  Optional<GenericEventRecord> getEventByEventId(String eventId);
  List<GenericEventRecord> getByKind(Kind kind);
  <U extends EventIF> void deleteEvent(U eventIF);
  <T> List<T> getAllDeletionEventIds();

  default GenericEventRecord createGenericEventRecordFromEntityIF(EventIF eventIF) {
    return new GenericEventRecord(
        eventIF.getId(),
        eventIF.getPublicKey(),
        eventIF.getCreatedAt(),
        eventIF.getKind(),
        eventIF.getTags(),
        eventIF.getContent(),
        eventIF.getSignature());
  }

  @SneakyThrows
  default <T extends BaseEvent> T createBaseEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind) {
    assertNotNull(baseEventFromKind);
    Constructor<T> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return constructor.newInstance(genericEventRecord);
  }
}
