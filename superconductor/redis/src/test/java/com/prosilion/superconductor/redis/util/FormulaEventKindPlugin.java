package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;

  public FormulaEventKindPlugin(
      @NonNull EventPluginIF eventPluginIF,
      @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF) {
    super(eventPluginIF);
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF incomingFormulaEvent) {
    FormulaEvent materialize = cacheFormulaEventServiceIF.materialize(incomingFormulaEvent);
    return super.processIncomingEvent(materialize);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}

