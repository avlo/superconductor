package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadgeSetsEventKindPlugin extends NonPublishingEventKindPlugin {
  public BadgeSetsEventKindPlugin(@NonNull EventPlugin eventPlugin) {
    super(eventPlugin);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
