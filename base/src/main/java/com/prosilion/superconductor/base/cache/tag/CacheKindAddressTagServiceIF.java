package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheKindAddressTagServiceIF {
      List<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull AddressTag t);
      List<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  Optional<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, String relayUrl);
}
