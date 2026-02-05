package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.List;

public interface RedisCacheServiceIF extends CacheServiceIF {
  //  Optional<GenericEventRecord> getRedisEventByUid(String id);
  //  Optional<GenericEventRecord> getEvent(EventIF eventIF);
  <T> List<T> getAllDeletionEventIds();

  //  TODO: below are currently unused, potentially for removal since 
//   also not spec'd in reference JpaCacheServiceIF
  List<GenericEventRecord> getEventsByKindAndIdentifierTag(Kind kind, IdentifierTag identifierTag);
}
