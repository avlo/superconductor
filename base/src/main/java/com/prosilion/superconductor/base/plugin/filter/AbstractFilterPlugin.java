package com.prosilion.superconductor.base.plugin.filter;

import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public abstract class AbstractFilterPlugin implements FilterPlugin {
  private final String code;

  protected AbstractFilterPlugin(@NonNull String code) {
    this.code = code;
  }
}
