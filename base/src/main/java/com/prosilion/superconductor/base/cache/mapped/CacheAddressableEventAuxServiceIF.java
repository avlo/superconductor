package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.nostr.tag.SetsPairedEventTagIF;
import java.util.Optional;
import lombok.NonNull;

public interface CacheAddressableEventAuxServiceIF<T extends SetsPairedEventTagIF, U extends ReferencedAbstractEventTag> {
  Optional<T> materialize(@NonNull EventIF eventIF, Relay relay);
  //  Optional<T> getAddressTagEvent(@NonNull GenericEventRecord genericEventRecord);
//  Optional<T> getEvent(@NonNull String eventId, Relay relay);

  //  Optional<T> getBy(U referencedAbstractEventTag);
  Kind getKind();
}
