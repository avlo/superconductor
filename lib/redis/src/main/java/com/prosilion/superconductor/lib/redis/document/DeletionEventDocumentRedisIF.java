package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.superconductor.base.DeletionEntityIF;

public interface DeletionEventDocumentRedisIF extends DeletionEntityIF<String> {
  String getId();
}
