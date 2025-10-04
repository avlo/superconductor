package com.prosilion.superconductor.base.controller;

public interface EventApiUiIF extends ApiUiIF {
  String getEventUiHtmlFile();

  default String getHtmlFile() {
    return getEventUiHtmlFile();
  }
}
