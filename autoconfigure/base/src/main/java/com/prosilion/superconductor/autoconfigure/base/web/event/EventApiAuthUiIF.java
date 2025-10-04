package com.prosilion.superconductor.autoconfigure.base.web.event;

import com.prosilion.superconductor.base.controller.EventApiUiIF;

public interface EventApiAuthUiIF extends EventApiUiIF {
  String getEventAuthHtmlFile();

  @Override
  default String getEventUiHtmlFile() {
    return getEventAuthHtmlFile();
  }
}
