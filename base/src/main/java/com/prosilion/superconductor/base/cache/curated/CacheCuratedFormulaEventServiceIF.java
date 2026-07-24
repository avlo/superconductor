package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;

public interface CacheCuratedFormulaEventServiceIF extends CacheFormulaEventServiceIF, CacheCuratedEventServiceIF<FormulaEvent> {
}
