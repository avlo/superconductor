package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;

public interface CacheBadgeAwardGenericEventServiceIF extends CacheBadgeAwardAbstractEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardCanonicalEvent> {
}
