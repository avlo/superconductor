package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;

public interface RedisCacheServiceIF extends CacheServiceIF {
  List<String> getAllDeletionEventIds();
  List<? extends BaseEvent> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencedPubKeyTag);
  List<? extends BaseEvent> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencedPubKeyTag, AddressTag addressTag);
  List<? extends BaseEvent> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag);
  List<? extends BaseEvent> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  List<? extends BaseEvent> getEventsByKindAndIdentifierTag(Kind kind, IdentifierTag identifierTag);
}
