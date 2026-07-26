package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedFormulaEventServiceIF extends CacheTagMappedEventServiceIF<CuratedFormulaEvent, AddressTag>, CacheCuratedEventServiceIF<CuratedFormulaEvent> {
  Optional<CuratedFormulaEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull Relay relay);
  Optional<CuratedFormulaEvent> getByDirect(@NonNull EventTag eventTag);
}
