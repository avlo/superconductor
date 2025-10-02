package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.superconductor.base.DeletionEventIF;

public interface DeletionEventNosqlEntityIF extends DeletionEventIF<String> {
  String getEventId();
}
