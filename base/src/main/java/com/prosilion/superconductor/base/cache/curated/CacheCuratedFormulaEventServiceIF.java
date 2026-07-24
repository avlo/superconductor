package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedFormulaEventServiceIF extends CacheTagMappedEventServiceIF<CuratedFormulaEvent, AddressTag>, EventMaterializer<CuratedFormulaEvent>, CacheCuratedEventServiceIF<CuratedFormulaEvent> {
  Optional<CuratedFormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay);
  CuratedFormulaEvent createFromFetched(@NonNull FormulaEvent formulaEvent, @NonNull Relay relay);
}
