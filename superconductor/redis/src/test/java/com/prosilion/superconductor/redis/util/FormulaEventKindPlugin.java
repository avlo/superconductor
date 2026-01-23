package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.type.NonPublishingEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;

  public FormulaEventKindPlugin(
      @NonNull EventKindPluginIF eventKindPlugin,
      @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF) {
    super(eventKindPlugin);
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF incomingFormulaEvent) {
    super.processIncomingEvent(
        cacheFormulaEventServiceIF.materialize(
            incomingFormulaEvent));
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}

