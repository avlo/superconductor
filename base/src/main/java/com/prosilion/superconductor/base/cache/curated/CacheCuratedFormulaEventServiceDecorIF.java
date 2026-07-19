package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.FormulaEvent;

public interface CacheCuratedFormulaEventServiceDecorIF extends CacheFormulaEventServiceDecorIF,
   CacheCuratedEventServiceIF<FormulaEvent> {
}
