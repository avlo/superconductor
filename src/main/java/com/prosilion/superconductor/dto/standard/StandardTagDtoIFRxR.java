package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;

public interface StandardTagDtoIFRxR {
  String getCode();
  <T extends StandardTagEntityRxR> T convertDtoToEntity();
}
