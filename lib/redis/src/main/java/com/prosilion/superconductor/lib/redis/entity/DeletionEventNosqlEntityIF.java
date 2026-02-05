package com.prosilion.superconductor.lib.redis.entity;

import com.prosilion.superconductor.base.service.event.DeletionEventIF;

public interface DeletionEventNosqlEntityIF extends DeletionEventIF<String> {
  String getEventId();
}
