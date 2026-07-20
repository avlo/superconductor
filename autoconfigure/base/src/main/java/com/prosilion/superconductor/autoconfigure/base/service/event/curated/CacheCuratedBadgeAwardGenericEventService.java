package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeAwardEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// TODO: rxr common elements from CacheCuratedBadgeDefinitionGenericEventService into baseClass
public class CacheCuratedBadgeAwardGenericEventService extends CacheCuratedEventService<CuratedBadgeAwardGenericEvent> implements CacheCuratedBadgeAwardEventServiceIF {

  public CacheCuratedBadgeAwardGenericEventService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> materialize(@NonNull EventIF incomingCurationSetsEvent) {
    return Optional.of(new CuratedBadgeAwardGenericEvent(incomingCurationSetsEvent.asGenericEventRecord()));
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndEventTag(getKind(), eventTag));
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag)).toList();
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag));
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag)).toList();
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(getKind(), pubKeyTag, addressTag));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS;
  }
}
