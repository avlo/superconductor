package com.prosilion.superconductor.autoconfigure.base.web.event;

import com.prosilion.superconductor.base.controller.EventApiUiIF;

public interface EventApiNoAuthUiIF extends EventApiUiIF {
  String getEventNoAuthHtmlFile();

  @Override
  default String getEventUiHtmlFile() {
    return getEventNoAuthHtmlFile();
  }
}
