package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.List;

public interface RedisCacheServiceRxRIF extends CacheServiceIF {
  <T extends BaseEvent> List<T> getEventsByKindAndIdentifierTag(Kind kind, IdentifierTag identifierTag);
}
