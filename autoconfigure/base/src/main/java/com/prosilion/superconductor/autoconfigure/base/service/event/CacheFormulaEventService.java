package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "FormulaEvent [%s] is missing required AddressTag";
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheFormulaEventService(
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  //  TODO: duplicate in @CacheFollowsEventSeervice, consolidate  
  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize);
  }

  @Override
  public Optional<FormulaEvent> materialize(@NonNull EventIF incomingFormulaEvent) {
    log.debug("inside materialize(EventIF incomingFormulaEvent):\n  {}", incomingFormulaEvent.createPrettyPrintJson());
    return cacheReferenceAddressTagServiceIF
       .getBy(incomingFormulaEvent.requireFirstTag(AddressTag.class))
       .map(BadgeDefinitionGenericEvent::new)
       .map(event ->
          new FormulaEvent(
             incomingFormulaEvent.asGenericEventRecord(),
             addressTag -> event));
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    return cacheReferenceAddressTagServiceIF
       .getBy(
          new AddressTag(
             Kind.ARBITRARY_CUSTOM_APP_DATA,
             publicKey,
             identifierTag,
             relay))
       .stream().findFirst()
       .flatMap(this::getFormulaEventById);
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull AddressTag addressTag) {
    return cacheKindAddressTagServiceIF
       .getDirectBy(
          Kind.ARBITRARY_CUSTOM_APP_DATA,
          addressTag)
       .stream().findFirst()
       .flatMap(this::getFormulaEventById);
  }

  private Optional<FormulaEvent> getFormulaEventById(GenericEventRecord genericEventRecord) {
    return getEvent(
       genericEventRecord.getId(),
       genericEventRecord.getRelayTag().map(RelayTag::getRelay).orElse(null));
  }

  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
