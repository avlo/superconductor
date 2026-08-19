package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  public FormulaEventKindPlugin(@NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
