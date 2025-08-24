package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentIF;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import java.util.List;
import org.springframework.lang.NonNull;

public interface RedisCacheServiceIF extends CacheServiceIF<EventDocumentIF, EventDocumentIF> {
  List<DeletionEventDocumentIF> getAllDeletionEvents();
  List<EventDocumentIF> getEventsByKindAndPubKeyTag(@NonNull Kind kind, @NonNull PublicKey publicKey);
}
