package com.prosilion.superconductor.autoconfigure.base.web.req;

import com.prosilion.superconductor.base.controller.ReqApiUiIF;

public interface ReqApiAuthUiIF extends ReqApiUiIF {
  String getReqAuthHtmlFile();

  @Override
  default String getReqUiHtmlFile() {
    return getReqAuthHtmlFile();
  }
}
