package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.util.Optional;
import lombok.NonNull;

public interface CacheAddressableEventServiceIF<T extends AddressableEvent, U extends ReferencedAbstractEventTag> {
  Optional<T> materialize(@NonNull EventIF eventIF);
  Optional<T> getByExpanded(U referencedAbstractEventTag);
  Optional<T> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
  Kind getKind();
}
