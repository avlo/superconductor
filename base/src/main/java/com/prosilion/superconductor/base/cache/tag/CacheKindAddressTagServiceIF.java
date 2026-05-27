package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheKindAddressTagServiceIF {
  Optional<GenericEventRecord> getEventByKindAndAddressTag(@NonNull Kind kind, @NonNull AddressTag t);
  Optional<GenericEventRecord> getEventByKindAndPubKeyTagAndAddressTag(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  Optional<GenericEventRecord> getEventByKindAndPubKeyTagAndIdentifierTag(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, String relayUrl);
}
