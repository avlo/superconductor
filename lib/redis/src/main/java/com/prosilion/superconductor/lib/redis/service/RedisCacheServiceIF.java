package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;

public interface RedisCacheServiceIF extends CacheServiceIF {
  Optional<GenericEventRecord> getRedisEventByUid(String id);
  //  Optional<GenericEventRecord> getEvent(EventIF eventIF);
  List<String> getAllDeletionEventIds();

  //  TODO: below are currently unused, potentially for removal since 
//   also not spec'd in reference JpaCacheServiceIF
  List<GenericEventRecord> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencedPubKeyTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencedPubKeyTag, AddressTag addressTag);
  List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag);
  List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag);
  List<GenericEventRecord> getEventsByKindAndIdentifierTag(Kind kind, IdentifierTag identifierTag);
}
