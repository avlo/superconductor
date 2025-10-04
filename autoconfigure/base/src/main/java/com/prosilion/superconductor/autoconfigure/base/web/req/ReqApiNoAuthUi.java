package com.prosilion.superconductor.autoconfigure.base.web.req;

public class ReqApiNoAuthUi implements ReqApiNoAuthUiIF {
  @Override
  public String getReqNoAuthHtmlFile() {
    return "thymeleaf/request-test";
  }
}
