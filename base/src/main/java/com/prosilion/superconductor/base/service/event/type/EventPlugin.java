package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TagMappedEventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.lang.NonNull;

@Slf4j
public class EventPlugin implements EventPluginIF {
  private final MultiValuedMap<Kind, CacheTagMappedEventServiceIF<TagMappedEventIF>> kindClassMap = new ArrayListValuedHashMap<>();
  private final CacheServiceIF cacheServiceIF;

  public EventPlugin(
      @NonNull List<CacheTagMappedEventServiceIF<TagMappedEventIF>> cacheEventTagBaseEventServiceIFS,
      @NonNull CacheServiceIF cacheServiceIF) {
    log.info("class {} adding:", getClass().getSimpleName());
    cacheEventTagBaseEventServiceIFS.forEach(kindClassMap ->
        add(kindClassMap.getKind(), kindClassMap));
    this.cacheServiceIF = cacheServiceIF;
  }

  public void add(@NonNull Kind kind, @NonNull CacheTagMappedEventServiceIF<TagMappedEventIF> value) {
    log.info("mapping kind [{}] to class [{}]...", kind.getName(), value.getClass().getSimpleName());
    boolean containsKey = kindClassMap.containsKey(kind);
    kindClassMap.put(kind, value);
    log.info("...done");
  }

  @Override
//  TODO: needs clean refactor
  public void processIncomingEvent(EventIF event) {
    log.info("{} processIncomingEvent() called with event: {}", getClass().getSimpleName(), event);
    Kind kind = event.getKind();
    if (!containsKind(kind)) {
      log.info("saving canonical event...");
      cacheServiceIF.save(event);
      log.info("...done");
      return;
    }

    doMultiMappedEKind(event);
  }

  private void doMultiMappedEKind(EventIF event) {
    Kind kind = event.getKind();
    Collection<CacheTagMappedEventServiceIF<TagMappedEventIF>> cacheTagMappedEventServiceIFs = kindClassMap.get(kind);
    if (!(kind.equals(Kind.BADGE_AWARD_EVENT))) {
      Collection<CacheTagMappedEventServiceIF<TagMappedEventIF>> cacheTagMappedEventServiceIFS = kindClassMap.get(Kind.BADGE_DEFINITION_EVENT);
      badgeDefinitionEventCondition(event, cacheTagMappedEventServiceIFS);
      return;
    }

    try {
      badgeAwardEventCondition(event, cacheTagMappedEventServiceIFs);
    } catch (NostrException e) {
      throw new RuntimeException(e);
    }
  }

  private void badgeAwardEventCondition(EventIF event, Collection<CacheTagMappedEventServiceIF<TagMappedEventIF>> cacheTagMappedEventServiceIFs) throws NostrException {
    if (hasExternalIdentityTag(event)) {
      CacheTagMappedEventServiceIF<TagMappedEventIF> anIf = cacheTagMappedEventServiceIFs.stream().filter(CacheBadgeAwardReputationEventServiceIF.class::isInstance).findFirst().orElseThrow(() -> new NostrException("badgeAwardEventCondition blew up"));
      log.info("saving CacheBadgeAwardGenericEvent event...");
      anIf.save((TagMappedEventIF) event);
      log.info("...done");
      return;
    }

    CacheTagMappedEventServiceIF<TagMappedEventIF> anIf = cacheTagMappedEventServiceIFs.stream()
        .filter(Predicate.not(CacheBadgeAwardReputationEventServiceIF.class::isInstance)).findFirst().orElseThrow();
    log.info("saving CacheBadgeAwardReputationEvent event...");
    cacheServiceIF.save(event);
    log.info("...done");
  }

  private void badgeDefinitionEventCondition(EventIF event, Collection<CacheTagMappedEventServiceIF<TagMappedEventIF>> cacheTagMappedEventServiceIFs) {
    if (hasExternalIdentityTag(event)) {
      CacheTagMappedEventServiceIF<TagMappedEventIF> anIf = cacheTagMappedEventServiceIFs.stream().filter(CacheBadgeDefinitionReputationEventServiceIF.class::isInstance).findFirst().orElseThrow();
      log.info("saving CacheBadgeDefinitionReputationEvent event...");
      anIf.save((TagMappedEventIF) event);
      log.info("...done");
      return;
    }

    log.info("saving CacheBadgeDefinitionAwardEvent event...");
    cacheServiceIF.save(event);
    log.info("...done");
  }

  private boolean hasExternalIdentityTag(EventIF event) {
    boolean hasExternalIdentityTag = !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).isEmpty();
    log.info("event has ExternalIdentityTag? [{}].  return {}", hasExternalIdentityTag, hasExternalIdentityTag);
    return hasExternalIdentityTag;
  }

  private boolean containsKind(Kind kind) {
    boolean containsKey = kindClassMap.containsKey(kind);
    if (containsKey) {
      log.info("kindClassMap:\n  {}\n  did not contain kind [{}].  return false", kindClassMap, kind);
      return true;
    }
    return false;
  }
}
