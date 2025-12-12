package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.TagMappedEventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public void save(@NonNull FormulaEvent incomingFormulaEvent) {
    Optional<GenericEventRecord> optionalFormulaEvent = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        incomingFormulaEvent.getKind(),
        incomingFormulaEvent.getPublicKey(),
        incomingFormulaEvent.getIdentifierTag()).stream().findFirst();

    if (optionalFormulaEvent.isEmpty()) {
      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
      cacheServiceIF.save(incomingFormulaEvent);
      log.debug("...done");
      return;
    }

    FormulaEvent existingFormulaEvent = getEvent(optionalFormulaEvent.get().getId()).orElseThrow();
    if (!softEquals(incomingFormulaEvent, existingFormulaEvent)) {
      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
      cacheServiceIF.save(incomingFormulaEvent);
      log.debug("...done");
      return;
    }

    if (!identifierTagEquals(incomingFormulaEvent, existingFormulaEvent)) {
      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
      cacheServiceIF.save(incomingFormulaEvent);
      log.debug("...done");
      return;
    }
    if (!incomingFormulaEvent.getFormula().equals(existingFormulaEvent.getFormula()))
      throw new NostrException(
          String.format("Incoming FormulaEvent [%s] formula clashes with pre-existing FormulaEvent [%s] formula having same Kind, PublicKey, Uuid", incomingFormulaEvent, existingFormulaEvent));
  }

  private static boolean softEquals(TagMappedEventIF incomingFormulaEvent, TagMappedEventIF existingFormulaEvent) {
    return existingFormulaEvent.getKind().equals(incomingFormulaEvent.getKind()) &&
        existingFormulaEvent.getPublicKey().equals(incomingFormulaEvent.getPublicKey());
  }

  private static boolean identifierTagEquals(@NonNull FormulaEvent incomingFormulaEvent, FormulaEvent existingFormulaEvent) {
    return existingFormulaEvent.getIdentifierTag().equals(incomingFormulaEvent.getIdentifierTag());
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedFormulaEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedFormulaEvent.isEmpty())
      return Optional.empty();

    return Optional.of(cacheDereferenceAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedFormulaEvent.orElseThrow(),
        FormulaEvent.class,
        (Function<AddressTag, BadgeDefinitionAwardEvent>) addressTag ->
            getBadgeDefinitionAwardEvent(
                unpopulatedFormulaEvent.get())));
  }

  private BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(GenericEventRecord genericEventRecord) {
    List<BaseTag> baseTags = genericEventRecord.getTags();

    AddressTag firstAddressTag = baseTags.stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast).findFirst().orElseThrow();

    GenericEventRecord firstAddressTagAsEvent = cacheDereferenceAddressTagServiceIF.getEvent(firstAddressTag).orElseThrow();

    BadgeDefinitionAwardEvent addressTagNowBadgeDefinitionAwardEvent =
        cacheServiceIF.createTypedSimpleEvent(
            firstAddressTagAsEvent,
            BadgeDefinitionAwardEvent.class);
    return addressTagNowBadgeDefinitionAwardEvent;
  }

  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
