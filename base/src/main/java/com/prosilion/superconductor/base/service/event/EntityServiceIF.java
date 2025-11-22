package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import java.util.Optional;

public interface EntityServiceIF<T, U extends EventIF> {
  T save(EventIF event);
  List<U> getAll();
  Optional<U> findByEventIdString(String eventId);
  List<U> getEventsByKind(Kind kind);
  List<U> getEventsByKindAndAuthorPublicKey(Kind kind, PublicKey authorPublicKey);
  List<U> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencePubKeyTag);
  List<U> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencePubKeyTag, AddressTag addressTag);
  List<U> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag);
  List<U> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
}
