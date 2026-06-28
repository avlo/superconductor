package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeAwardGenericEventAuxServiceIF<S extends BadgeDefinitionGenericEventAux, T extends BadgeAwardGenericEventAux<S>> extends CacheTagMappedEventServiceIF<T, AddressTag>, EventMaterializer<T> {

  @Override
  Optional<T> materialize(@NonNull EventIF eventIF);
}
