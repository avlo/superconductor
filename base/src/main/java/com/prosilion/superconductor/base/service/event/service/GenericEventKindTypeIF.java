package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.KindTypeIF;

public interface GenericEventKindTypeIF extends GenericEventKindIF {
  KindTypeIF getKindType();
}
