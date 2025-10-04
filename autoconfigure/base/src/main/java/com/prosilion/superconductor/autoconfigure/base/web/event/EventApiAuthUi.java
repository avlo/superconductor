package com.prosilion.superconductor.autoconfigure.base.web.event;

public class EventApiAuthUi implements EventApiAuthUiIF {
  @Override
  public String getEventAuthHtmlFile() {
    return "thymeleaf/api-tests-auth";
  }
}
