package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent, AddressTag>, EventMaterializer<FormulaEvent> {
  @Override
  Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull String url);
  @Override
  Optional<FormulaEvent> materialize(@NonNull EventIF eventIF);
  Optional<FormulaEvent> getBy(@NonNull AddressTag addressTag);
  Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay);
}
