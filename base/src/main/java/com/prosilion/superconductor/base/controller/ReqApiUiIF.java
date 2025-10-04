package com.prosilion.superconductor.base.controller;

public interface ReqApiUiIF extends ApiUiIF {
  String getReqUiHtmlFile();

  default String getHtmlFile() {
    return getReqUiHtmlFile();
  }
}
