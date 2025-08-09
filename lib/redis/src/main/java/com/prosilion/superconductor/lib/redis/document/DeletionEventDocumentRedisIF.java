package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.superconductor.base.DeletionEventIF;

public interface DeletionEventDocumentRedisIF extends DeletionEventIF<String> {
  String getEventId();
}
