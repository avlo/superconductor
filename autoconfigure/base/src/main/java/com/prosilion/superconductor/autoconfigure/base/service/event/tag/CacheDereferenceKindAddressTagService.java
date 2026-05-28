package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceKindAddressTagService extends CacheDereferenceBaseAbstractTagService<AddressTag> implements CacheKindAddressTagServiceIF {
  public CacheDereferenceKindAddressTagService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  @Override
  public Optional<GenericEventRecord> getEventByKindAndPubKeyTagAndAddressTag(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    Optional<GenericEventRecord> eventByKindAndAddressTag = getEventByKindAndAddressTag(kind, addressTag);
    Optional<GenericEventRecord> first = eventByKindAndAddressTag.stream().filter(genericEventRecord ->
            isContains(pubKeyTag, genericEventRecord))
        .distinct().findFirst();
    return first;
  }

  private boolean isContains(@NonNull PubKeyTag pubKeyTag, GenericEventRecord genericEventRecord) {
    List<PubKeyTag> typeSpecificTags = genericEventRecord.getTypeSpecificTags(PubKeyTag.class);
    List<PublicKey> list = typeSpecificTags
        .stream().map(PubKeyTag::getPublicKey).toList();
    boolean contains = list
        .contains(pubKeyTag.getPublicKey());
    return contains;
  }

  @Override
  public Optional<GenericEventRecord> getEventByKindAndPubKeyTagAndIdentifierTag(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull String relayUrl) {
    Optional<GenericEventRecord> localGenericEventRecordOptional = getLocalEventFxn(kind, pubKeyTag, identifierTag);

    if (localGenericEventRecordOptional.isPresent()) {
      log.debug("... localGenericEventRecordOptional found locally:\n  {}",
          localGenericEventRecordOptional.get().createPrettyPrintJson());
      return localGenericEventRecordOptional;
    }

    log.debug("local addressTag not found, calling getRemoteEventGenericEventRecord ...");
    return getRemoteEventGenericEventRecord(kind, pubKeyTag, identifierTag, relayUrl);
  }

  @Override
  public Optional<GenericEventRecord> getEventByKindAndAddressTag(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    log.debug("inside getEventByKindAndAddressTag(Kind kind, AddressTag) with:\n  kind: [{}]\naddressTag:  {}", kind, Util.prettyPrintAddressTags(addressTag));
    log.debug("... calling getLocalEventFxn(kind, addressTag) ...");
    Optional<GenericEventRecord> localGenericEventRecordOptional = getLocalEventFxn(kind, addressTag);

    if (localGenericEventRecordOptional.isPresent()) {
      log.debug("... localGenericEventRecordOptional found locally:\n  {}",
          localGenericEventRecordOptional.get().createPrettyPrintJson());
      return localGenericEventRecordOptional;
    }

    String recommendedRelayUrl = Optional.ofNullable(addressTag.getRelay().getUrl()).orElseThrow(() ->
        new NostrException(
            String.format("addressTag [%s] does not contain a (valid) url", addressTag)));

    log.debug("local addressTag not found, calling getRemoteEventGenericEventRecord ...");
    return getRemoteEventGenericEventRecord(kind, addressTag, recommendedRelayUrl);
  }

  private Optional<GenericEventRecord> getRemoteEventGenericEventRecord(Kind kind, AddressTag addressTag, String relayUrl) {
    Filters abstractTagFilters = getAbstractTagFilters(addressTag);
    abstractTagFilters.add(new KindFilter(kind));
    log.debug("getRemoteEventGenericEventRecord filters:\n  {}", abstractTagFilters);
    Optional<GenericEventRecord> optionalGenericEventRecord = sendConsolidatorReq(
        relayUrl,
        abstractTagFilters);

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }

  private Optional<GenericEventRecord> getRemoteEventGenericEventRecord(Kind kind, PubKeyTag pubKeyTag, IdentifierTag identifierTag, String relayUrl) {
    Filters abstractTagFilters = new Filters(
        new KindFilter(kind),
        new ReferencedPublicKeyFilter(pubKeyTag),
        new IdentifierTagFilter(identifierTag));
    log.debug("getRemoteEventGenericEventRecord filters:\n  {}", abstractTagFilters);
    Optional<GenericEventRecord> optionalGenericEventRecord = sendConsolidatorReq(
        relayUrl,
        abstractTagFilters);

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }

  private Optional<GenericEventRecord> getLocalEventFxn(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    log.debug("inside getLocalEventFxn(Kind kind, AddressTag) with\n  kind:{}\naddressTag:  {}", kind, Util.prettyPrintAddressTags(addressTag));

    List<GenericEventRecord> eventsByKindAndAddressTag = cacheServiceIF.getEventsByKindAndAddressTag(kind, addressTag);
    log.debug("found List<GenericEventRecord> eventsByKindAndAddressTag size: [{}]", eventsByKindAndAddressTag.size());
    log.debug("found List<GenericEventRecord> eventsByKindAndAddressTag:\n  {}", Util.prettyPrintGenericEventRecords(eventsByKindAndAddressTag));

    boolean hasContents = !eventsByKindAndAddressTag.isEmpty();
    if (hasContents) {
      Optional<GenericEventRecord> first = eventsByKindAndAddressTag.stream().findFirst();
      log.debug("... returning first local KindAndAddressTag as GER:\n  {}", first.get().createPrettyPrintJson());
      return first;
    }

    log.debug("getLocalEventFxn(addressTag) not found, return EMPTY");
    return Optional.empty();
  }

  private Optional<GenericEventRecord> getLocalEventFxn(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    log.debug("inside getLocalEventFxn(Kind kind, IdentifierTag) with\n  kind:{}\nidentifierTag:  {}", kind, identifierTag);

    List<GenericEventRecord> eventsByKindAndPubKeyTagAndIdentifierTag = cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, pubKeyTag, identifierTag);
    log.debug("found List<GenericEventRecord> eventsByKindAndPubKeyTagAndIdentifierTag size: [{}]", eventsByKindAndPubKeyTagAndIdentifierTag.size());
    log.debug("found List<GenericEventRecord> eventsByKindAndPubKeyTagAndIdentifierTag:\n  {}", Util.prettyPrintGenericEventRecords(eventsByKindAndPubKeyTagAndIdentifierTag));

    boolean hasContents = !eventsByKindAndPubKeyTagAndIdentifierTag.isEmpty();
    if (hasContents) {
      Optional<GenericEventRecord> first = eventsByKindAndPubKeyTagAndIdentifierTag.stream().findFirst();
      log.debug("... returning local eventsByKindAndPubKeyTagAndIdentifierTag as GER:\n  {}", first.get().createPrettyPrintJson());
      return first;
    }

    log.debug("getLocalEventFxn(kind, publicKey, identifierTag) not found, return EMPTY");
    return Optional.empty();
  }

  @Override
  Filters getAbstractTagFilters(AddressTag addressTag) {
    return new Filters(new AddressTagFilter(addressTag));
  }
}
