package com.prosilion.superconductor.autoconfigure.base.web.req;

import com.prosilion.superconductor.base.controller.ReqApiUiIF;

public interface ReqApiNoAuthUiIF extends ReqApiUiIF {
  String getReqNoAuthHtmlFile();

  @Override
  default String getReqUiHtmlFile() {
    return getReqNoAuthHtmlFile();
  }
}
