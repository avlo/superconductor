package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends CacheTagMappedEventServiceIF<T, AddressTag>, EventMaterializer<T> {

  @Override
  Optional<T> materialize(@NonNull EventIF eventIF);
}
