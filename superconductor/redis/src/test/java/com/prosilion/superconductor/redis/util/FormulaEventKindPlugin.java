package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
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
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T incomingFormulaEvent) {
    super.processIncomingEvent(
        cacheFormulaEventServiceIF.materialize(
            incomingFormulaEvent));
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }

  @Override
  public FormulaEvent materialize(EventIF eventIF) {
    return cacheFormulaEventServiceIF.materialize(eventIF);
  }
}

