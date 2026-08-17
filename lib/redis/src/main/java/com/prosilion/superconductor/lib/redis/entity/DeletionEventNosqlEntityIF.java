package com.prosilion.superconductor.lib.redis.entity;

import com.prosilion.superconductor.base.cache.DeletionEventIF;

public interface DeletionEventNosqlEntityIF extends DeletionEventIF<String> {
  String getEventId();
}
