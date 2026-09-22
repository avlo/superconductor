package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.condition.EventCurationInactiveCondition;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardCanonicalEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginRxR;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeAwardCanonicalEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.FormulaEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Slf4j
@AutoConfiguration
@Conditional(EventCurationInactiveCondition.class)
public class EventCurationInactiveConfig {
  @ConditionalOnMissingBean
  @Bean("badgeDefinitionGenericEventKindPlugin")
  public BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin(
     @NonNull EventPlugin eventPlugin) {
    return new BadgeDefinitionGenericEventKindPlugin(eventPlugin);
  }

  @Bean("badgeAwardCanonicalEventKindPlugin")
  @ConditionalOnMissingBean(name = "badgeAwardCanonicalEventKindPlugin")
  BadgeAwardCanonicalEventKindPlugin badgeAwardCanonicalEventKindPlugin(
     @NonNull NotifierService notifierService,
     @NonNull EventPluginRxR<BadgeAwardCanonicalEvent> eventPlugin) {
    return new BadgeAwardCanonicalEventKindPlugin(
       notifierService,
       eventPlugin);
  }

  @Bean("formulaEventKindPlugin")
  @ConditionalOnMissingBean(name = "formulaEventKindPlugin")
  FormulaEventKindPlugin formulaEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPlugin eventPlugin) {
    return new FormulaEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       eventPlugin);
  }

  @Bean("eventKindMaterializers")
  @ConditionalOnMissingBean(name = "eventKindMaterializers")
  Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> eventKindMaterializers(
     @NonNull CacheBadgeAwardCanonicalEventService cacheBadgeAwardCanonicalEventService,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService,
     @NonNull CacheFormulaEventService cacheFormulaEventService) {
    Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> kindFxnMap = new HashMap<>();
    kindFxnMap.put(
       Kind.BADGE_AWARD_EVENT,
       cacheBadgeAwardCanonicalEventService::materialize);

    kindFxnMap.put(
       Kind.BADGE_DEFINITION_EVENT,
       cacheBadgeDefinitionGenericEventService::materialize);

    kindFxnMap.put(
       Kind.ARBITRARY_CUSTOM_APP_DATA,
       cacheFormulaEventService::materialize);

    kindFxnMap.put(
       Kind.DELETION,
       eventIF -> Optional.of(new DeletionEvent(
          eventIF.asGenericEventRecord())));

    return kindFxnMap;
  }

  @Bean("eventKindTypeMaterializers")
  @ConditionalOnMissingBean(name = "eventKindTypeMaterializers")
  Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> emptyEventKindTypeMaterializers() {
    return Map.of();
  }
}
