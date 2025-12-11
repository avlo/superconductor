package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TagMappedEventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventPlugin implements EventPluginIF {
  private final Map<Kind, CacheTagMappedEventServiceIF<TagMappedEventIF>> kindClassMap = new HashMap<>();
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
    log.info("kind [{}] mapped to class [{}]...", kind.getName(), value.getClass().getSimpleName());
    kindClassMap.putIfAbsent(kind, value);
    log.info("...done");
  }

  @Override
//  TODO: needs clean refactor
  public void processIncomingEvent(EventIF event) {
    log.info("{} processIncomingEvent() called with event: {}", getClass().getSimpleName(), event);
    Kind kind = event.getKind();
    if (isCacheEventTagKind(event)) {
      log.info("saving CacheEventTagBaseEvent (EventTags) event...");
      kindClassMap.get(kind).save((TagMappedEventIF) event);
      log.info("...done");
      return;
    }

    log.info("saving canonical (!EventTags) event...");
    cacheServiceIF.save(event);
    log.info("...done");
  }

  //  TODO: needs clean refactor  
  private boolean isCacheEventTagKind(EventIF event) {
    Kind kind = event.getKind();
    if (!kindClassMap.containsKey(kind)) {
      log.info("kindClassMap:\n  {}\n  did not contain kind [{}].  return false", kindClassMap, kind);
      return false;
    }

    if (kind.equals(Kind.ARBITRARY_CUSTOM_APP_DATA)) {
      log.info("kind == Kind.ARBITRARY_CUSTOM_APP_DATA.  return true");
      return true;
    }

    boolean hasExternalIdentityTag = !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).isEmpty();
    log.info("event has ExternalIdentityTag? [{}].  return {}", hasExternalIdentityTag, hasExternalIdentityTag);
    return hasExternalIdentityTag;
  }
}
