package com.prosilion.superconductor.base.controller;

import org.springframework.lang.NonNull;

public class ApiUi implements ReqApiEventApiUi {
  private final ReqApiUiIF reqApiUiIF;
  private final EventApiUiIF eventApiUiIF;

  public ApiUi(@NonNull EventApiUiIF eventApiUiIF, @NonNull ReqApiUiIF reqApiUiIF) {
    this.eventApiUiIF = eventApiUiIF;
    this.reqApiUiIF = reqApiUiIF;
  }

  @Override
  public String getEventApiUi() {
    return eventApiUiIF.getEventUiHtmlFile();
  }

  @Override
  public String getReqApiUi() {
    return reqApiUiIF.getReqUiHtmlFile();
  }
}
