package com.prosilion.superconductor.autoconfigure.base.web.event;

public class EventApiNoAuthUi implements EventApiNoAuthUiIF {
  @Override
  public String getEventNoAuthHtmlFile() {
    return "thymeleaf/api-tests";
  }
}
