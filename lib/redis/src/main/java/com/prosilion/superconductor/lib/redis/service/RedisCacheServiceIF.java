package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import java.util.List;

public interface RedisCacheServiceIF extends CacheServiceIF<EventDocumentIF, EventDocumentIF> {
  List<DeletionEventDocumentRedisIF> getAllDeletionEvents();
}
