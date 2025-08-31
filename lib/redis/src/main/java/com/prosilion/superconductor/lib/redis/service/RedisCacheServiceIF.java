package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentIF;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import java.util.List;

public interface RedisCacheServiceIF extends CacheServiceIF<EventDocumentIF, EventDocumentIF> {
  List<DeletionEventDocumentIF> getAllDeletionEvents();
  List<EventDocumentIF> getEventsByKindAndPubKeyTag(Kind kind, PublicKey publicKey);
  List<EventDocumentIF> getEventsByKindAndUuid(Kind kind, String uuid);
}
