package com.prosilion.superconductor.lib.redis.entity;

import com.prosilion.superconductor.base.DeletionEventIF;

public interface DeletionEventNosqlEntityIF extends DeletionEventIF<String> {
  String getEventId();
}
