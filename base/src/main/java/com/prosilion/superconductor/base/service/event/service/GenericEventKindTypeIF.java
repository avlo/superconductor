package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.superconductor.base.service.event.type.KindTypeIF;

public interface GenericEventKindTypeIF extends GenericEventKindIF {
  KindTypeIF getKindType();
}
