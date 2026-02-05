package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

public interface CacheServiceIF {
  GenericEventRecord save(EventIF event);
  List<GenericEventRecord> getAll();
  Optional<GenericEventRecord> getEventByEventId(String eventId);
  List<GenericEventRecord> getByKind(Kind kind);
  List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(Kind kind, PublicKey authorPublicKey);
  List<GenericEventRecord> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencePubKeyTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencePubKeyTag, AddressTag addressTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag);
  List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  <U extends EventIF> void deleteEvent(U eventIF);
  <T> List<T> getAllDeletionEventIds();

  @SneakyThrows
  default <S extends BaseEvent> S createTypedSimpleEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<S> baseEventFromKind) {
    Constructor<S> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return constructor.newInstance(genericEventRecord);
  }
}
