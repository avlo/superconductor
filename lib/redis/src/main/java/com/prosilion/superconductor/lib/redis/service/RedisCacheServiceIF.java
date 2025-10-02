package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import java.util.List;

public interface RedisCacheServiceIF extends CacheServiceIF<EventNosqlEntityIF, EventNosqlEntityIF> {
  List<DeletionEventNosqlEntityIF> getAllDeletionEvents();
  List<EventNosqlEntityIF> getEventsByKindAndPubKeyTag(Kind kind, PublicKey publicKey);
  List<EventNosqlEntityIF> getEventsByKindAndUuid(Kind kind, String uuid);
}
