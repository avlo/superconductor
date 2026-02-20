package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Function<EventIF, FormulaEvent> eventMaterializer;

  public FormulaEventKindPlugin(
      @NonNull EventPlugin eventPlugin,
      @NonNull Function<EventIF, FormulaEvent> eventMaterializer) {
    super(eventPlugin);
    this.eventMaterializer = eventMaterializer;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF incomingFormulaEvent) {
    return super.processIncomingEvent(
        eventMaterializer.apply(incomingFormulaEvent));
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}

