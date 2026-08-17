package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface EntityServiceIF<T, U extends EventIF> {
  T save(EventIF event);
  List<U> getAll();
  Optional<U> findByEventIdString(String eventId);
  List<U> getEventsByKind(Kind kind);
  List<U> getEventsByPublicKey(@NonNull PublicKey publicKey);
  List<U> getEventsByKindAndAuthorPublicKey(Kind kind, PublicKey authorPublicKey);
  List<U> getEventsByKindAndPubKeyTag(Kind kind, PubKeyTag referencePubKeyTag);
  List<U> getEventsByKindAndEventTag(Kind kind, EventTag eventTag);
  List<U> getEventsByKindAndAddressTag(Kind kind, AddressTag addressTag);
  List<U> getEventsByKindAndIdentifierTag(@NonNull Kind kind, @NonNull IdentifierTag identifierTag);
  List<U> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PubKeyTag referencePubKeyTag, AddressTag addressTag);
  List<U> getEventsByKindAndPubKeyTagAndEventTag(Kind kind, PubKeyTag referencePubKeyTag, EventTag eventTag);
  List<U> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PubKeyTag referencedPubkeyTag, IdentifierTag identifierTag);
  Optional<U> getEventByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  Optional<U> getFirstEventByKindAndEventTag(Kind kind, EventTag eventTag);
  Optional<U> getFirstEventByKindAndAddressTag(@NonNull Kind kind, @NonNull AddressTag addressTag);
}
