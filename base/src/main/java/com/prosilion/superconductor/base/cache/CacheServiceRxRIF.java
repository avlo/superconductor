package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import java.util.Optional;

public interface CacheServiceRxRIF {
  <T extends BaseEvent> GenericEventRecord save(T event);
  <T extends BaseEvent> List<T> getAll();
  <T extends BaseEvent> Optional<T> getEventByEventId(String eventId);
  <T extends BaseEvent> List<T> getByKind(Kind kind);
  <T extends BaseEvent> List<T> getEventsByKindAndAuthorPublicKey(Kind kind, PublicKey authorPublicKey);
  <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTag(Kind kind, PubKeyTag referencePubKeyTag);
  <T extends BaseEvent> List<T> getEventsByKindAndEventTag(Kind kind, EventTag eventTag);
  <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTagAndEventTag(Kind kind, PubKeyTag referencePubKeyTag, EventTag eventTag);
  <T extends BaseEvent> List<T> getEventsByKindAndAddressTag(Kind kind, AddressTag addressTag);
  <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PubKeyTag referencePubKeyTag, AddressTag addressTag);
  <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PubKeyTag pubKeyTag, IdentifierTag identifierTag);
  <T extends BaseEvent> Optional<T> getEventByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  <T extends BaseEvent> Optional<T> getFirstEventByKindAndEventTag(Kind kind, EventTag eventTag);
  <T extends BaseEvent> Optional<T> getFirstEventByKindAndAddressTag(Kind kind, AddressTag addressTag);
  <T extends BaseEvent> void deleteEvent(T eventIF);
  <U> List<U> getAllDeletionEventIds();
  <T extends BaseEvent> List<T> getAllIncludingDeleted();
}
