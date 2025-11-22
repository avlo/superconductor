package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
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
  Optional<? extends EventTagsMappedEventsIF> getEvent(EventIF eventIF);
  Optional<? extends EventTagsMappedEventsIF> getEventByEventId(String eventId);
  List<? extends EventTagsMappedEventsIF> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencePubKeyTag);
  List<? extends EventTagsMappedEventsIF> getEventsByKindAndAuthorPublicKey(Kind kind, PublicKey authorPublicKey);
  List<? extends EventTagsMappedEventsIF> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencePubKeyTag, AddressTag addressTag);
  List<? extends EventTagsMappedEventsIF> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag);
  List<? extends EventTagsMappedEventsIF> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
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
