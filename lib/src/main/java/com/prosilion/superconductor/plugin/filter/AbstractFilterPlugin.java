package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.Filterable;
import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventDtoIF;

@Getter
public abstract class AbstractFilterPlugin<T extends Filterable, U extends GenericEventDtoIF> implements FilterPlugin<T, U> {
  private final String code;

  protected AbstractFilterPlugin(@NonNull String code) {
    this.code = code;
  }
}
