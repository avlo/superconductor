package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import java.util.Optional;

public interface CacheServiceIF {
  GenericEventRecord save(EventIF event);
  List<GenericEventRecord> getAll();
  Optional<GenericEventRecord> getEventByEventId(String eventId);
  List<GenericEventRecord> getByKind(Kind kind);
  List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(Kind kind, PublicKey authorPublicKey);
  List<GenericEventRecord> getEventsByKindAndPubKeyTag(Kind kind, PubKeyTag referencePubKeyTag);
  List<GenericEventRecord> getEventsByKindAndEventTag(Kind kind, EventTag eventTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndEventTag(Kind kind, PubKeyTag referencePubKeyTag, EventTag eventTag);
  List<GenericEventRecord> getEventsByKindAndAddressTag(Kind kind, AddressTag addressTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PubKeyTag referencePubKeyTag, AddressTag addressTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PubKeyTag pubKeyTag, IdentifierTag identifierTag);
  Optional<GenericEventRecord> getEventByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  Optional<GenericEventRecord> getFirstEventByKindAndEventTag(Kind kind, EventTag eventTag);
  Optional<GenericEventRecord> getFirstEventByKindAndAddressTag(Kind kind, AddressTag addressTag);
  <U extends EventIF> void deleteEvent(U eventIF);
  <T> List<T> getAllDeletionEventIds();
  List<GenericEventRecord> getAllIncludingDeleted();
}
