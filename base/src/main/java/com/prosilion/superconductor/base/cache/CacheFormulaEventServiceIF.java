package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent> {
  @Override
  Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull String url);
  @Override
  FormulaEvent materialize(@NonNull EventIF eventIF) throws NostrException;
}
