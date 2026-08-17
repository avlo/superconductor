package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;

public interface CacheBadgeAwardGenericEventServiceIF extends CacheBadgeAwardAbstractEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
}
