package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.NonNull;

public class EventPlugin implements EventPluginIF {
  private final Map<Kind, CacheEventTagBaseEventServiceIF> kindClassMap = new HashMap<>();
  private final CacheServiceIF cacheServiceIF;

  public EventPlugin(
      @NonNull List<CacheEventTagBaseEventServiceIF> cacheEventTagBaseEventServiceIFS,
      @NonNull CacheServiceIF cacheServiceIF) {
    cacheEventTagBaseEventServiceIFS.forEach(kindClassMap ->
        add(kindClassMap.getKind(), kindClassMap));
    this.cacheServiceIF = cacheServiceIF;
  }

  public void add(@NonNull Kind kind, @NonNull CacheEventTagBaseEventServiceIF value) {
    kindClassMap.putIfAbsent(kind, value);
  }

  @Override
  public void processIncomingEvent(EventIF event) {
    Kind kind = event.getKind();
    if (isABoolean(event)) {
      kindClassMap.get(kind).save(event);
      return;
    }
    
    cacheServiceIF.save(event);
  }

  private boolean isABoolean(EventIF event) {
    Kind kind = event.getKind();
    if (!kindClassMap.containsKey(kind)) {
      return false;
    }
    
    if (kind.equals(Kind.ARBITRARY_CUSTOM_APP_DATA)) {
      return true;
    }
    
    boolean hasExternalIdentityTag = !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).isEmpty();
    return hasExternalIdentityTag;
  }
}
