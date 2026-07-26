package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

final class CacheAsideEventLookup {
  private final RemoteEventQueryServiceIF remoteEventQueryServiceIF;

  CacheAsideEventLookup(@NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    this.remoteEventQueryServiceIF = remoteEventQueryServiceIF;
  }

  Optional<GenericEventRecord> findFirst(
     @NonNull Supplier<Optional<GenericEventRecord>> localLookup,
     @NonNull String relayUrl,
     @NonNull Filters filters) {
    return localLookup.get()
       .or(() -> remoteEventQueryServiceIF.sendRemoteReq(relayUrl, filters).stream().findFirst());
  }

  List<GenericEventRecord> findAll(
     @NonNull Supplier<List<GenericEventRecord>> localLookup,
     @NonNull String relayUrl,
     @NonNull Filters filters) {
    List<GenericEventRecord> localEvents = localLookup.get();
    return localEvents.isEmpty()
       ? remoteEventQueryServiceIF.sendRemoteReq(relayUrl, filters)
       : localEvents;
  }
}
