package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
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
    log.debug("processing incoming EventIF as FORMULA EVENT: [{}]", incomingFormulaEvent);
    FormulaEvent formulaEvent = new FormulaEvent(
        (GenericEventRecord) incomingFormulaEvent,
        addressTag ->
            cacheFormulaEventServiceIF.getBadgeDefinitionAwardEvent((GenericEventRecord) incomingFormulaEvent));

    FormulaEvent reconstructedFormulaEvent = cacheFormulaEventServiceIF.reconstruct(formulaEvent);
    super.processIncomingEvent(reconstructedFormulaEvent);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}

