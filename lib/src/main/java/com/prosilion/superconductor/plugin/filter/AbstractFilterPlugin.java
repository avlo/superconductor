package com.prosilion.superconductor.plugin.filter;

import lombok.Getter;
import lombok.NonNull;
import nostr.event.filter.Filterable;
import nostr.event.impl.GenericEvent;

@Getter
public abstract class AbstractFilterPlugin<T extends Filterable, U extends GenericEvent> implements FilterPlugin<T, U> {
  private final String code;

  protected AbstractFilterPlugin(@NonNull String code) {
    this.code = code;
  }
}
