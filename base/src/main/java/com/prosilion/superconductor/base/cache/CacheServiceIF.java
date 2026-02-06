package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import java.util.Optional;

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
}
