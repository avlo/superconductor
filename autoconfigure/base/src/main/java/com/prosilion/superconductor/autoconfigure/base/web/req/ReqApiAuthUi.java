package com.prosilion.superconductor.autoconfigure.base.web.req;

public class ReqApiAuthUi implements ReqApiAuthUiIF {
  @Override
  public String getReqAuthHtmlFile() {
    return "thymeleaf/request-test-auth";
  }
}
