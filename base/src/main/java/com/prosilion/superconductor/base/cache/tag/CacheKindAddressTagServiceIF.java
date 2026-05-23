package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheKindAddressTagServiceIF {
  Optional<GenericEventRecord> getEventByKindAndAddressTag(@NonNull Kind kind, @NonNull AddressTag t);
}
