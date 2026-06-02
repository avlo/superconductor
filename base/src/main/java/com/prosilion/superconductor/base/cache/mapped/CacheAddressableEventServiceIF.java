package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheAddressableEventServiceIF<T extends AddressableEvent, U extends ReferencedAbstractEventTag> {
  T materialize(@NonNull EventIF eventIF);
//  Optional<T> getAddressTagEvent(@NonNull GenericEventRecord genericEventRecord);

  Optional<T> getBy(U referencedAbstractEventTag);
  Kind getKind();
}
