package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import lombok.NonNull;

final class TagFilterFactory {
  private TagFilterFactory() {
  }

  static Filters forEvent(@NonNull EventTag eventTag) {
    return new Filters(new EventFilter(new GenericEventId(eventTag.getEventId())));
  }

  static Filters forAddressIdentity(@NonNull AddressTag addressTag) {
    return new Filters(
       new KindFilter(addressTag.getKind()),
       new AuthorFilter(addressTag.getPublicKey()),
       new IdentifierTagFilter(addressTag.requireIdentifierTag()));
  }

  static Filters forAddressReference(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    return new Filters(new AddressTagFilter(addressTag), new KindFilter(kind));
  }

  static Filters forReferencedPubKeyAndIdentifier(
     @NonNull Kind kind,
     @NonNull PubKeyTag pubKeyTag,
     @NonNull IdentifierTag identifierTag) {
    return new Filters(
       new KindFilter(kind),
       new ReferencedPublicKeyFilter(pubKeyTag),
       new IdentifierTagFilter(identifierTag));
  }
}
