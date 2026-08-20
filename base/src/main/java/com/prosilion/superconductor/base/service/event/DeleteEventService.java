package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteEventService implements DeleteEventServiceIF {
  private static final String DELETE_EVENT = "SuperConductor delete previous [%s] event, id: [%s]";
  private final String superconductorRelayUrl;
  private final Identity superconductorInstanceIdentity;
  private final CacheServiceIF cacheServiceIF;

  public DeleteEventService(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF) {
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF eventIF, @NonNull Relay relay) {
    DeletionEvent deletionEvent = new DeletionEvent(
       superconductorInstanceIdentity,
       new EventTag(eventIF.getId(), relay.getUrl()),
       String.format(
          DELETE_EVENT, eventIF.getKind(), eventIF.getId()),
       new Relay(superconductorRelayUrl));
    cacheServiceIF.deleteEvent(deletionEvent);
    log.debug("added deletion eventId [{}], marked as deleted eventId: [{}]",
       deletionEvent.getId(), eventIF.getId());
  }
}
