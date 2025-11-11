package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;

public interface RedisCacheServiceIF extends CacheServiceIF {
  List<String> getAllDeletionEventIds();
  List<GenericEventRecord> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencedPubKeyTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencedPubKeyTag, AddressTag addressTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag);
  List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  List<GenericEventRecord> getEventsByKindAndIdentifierTag(Kind kind, IdentifierTag identifierTag);
}
