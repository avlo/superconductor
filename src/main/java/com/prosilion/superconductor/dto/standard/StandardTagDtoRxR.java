package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;

public interface StandardTagDtoRxR {
  String getCode();
  StandardTagEntityRxR convertDtoToEntity();
}
