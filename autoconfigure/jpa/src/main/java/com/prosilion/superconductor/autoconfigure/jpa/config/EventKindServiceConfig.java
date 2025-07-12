package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import prosilion.superconductor.lib.jpa.event.JpaCache;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(JpaCache.class)
public class EventKindServiceConfig {

//  @Bean
//  @ConditionalOnMissingBean
//  ConcreteTagEntitiesService concreteTagEntitiesService(
//      @NonNull(List < TagPlugin < P, Q, R, S, T >> tagPlugins) {
//    return new ConcreteTagEntitiesService(tagPlugins);
//  }

//  @Bean
//  @ConditionalOnMissingBean
//  EventEntityService eventEntityService(
//      @NonNull ConcreteTagEntitiesService<
//          BaseTag,
//          AbstractTagEntityRepository<AbstractTagEntity>,
//          AbstractTagEntity,
//          EventEntityAbstractEntity,
//          EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>>
//          concreteTagEntitiesService,
//      @NonNull GenericTagEntitiesService genericTagEntitiesService,
//      @NonNull EventEntityRepository eventEntityRepository) {
//
//  }

//  @Bean

  /// /  @ConditionalOnMissingBean
  /// /  TODO: this should be moved into db-specific config for jpa & another should exist for redis
//  CacheIF jpaCache(
//      @NonNull EventEntityService eventEntityService,
//      @NonNull DeletionEventEntityService deletionEventEntityService) {
//    return new JpaCache(eventEntityService, deletionEventEntityService);
//  }
  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }

//  @Bean
//  @ConditionalOnMissingBean
//  DataLoader dataLoader(
//      @NonNull EventPluginIF eventPlugin,
//      @NonNull BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
//      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent) {
//    return new DataLoader(eventPlugin, upvoteBadgeDefinitionEvent, downvoteBadgeDefinitionEvent);
//  }
}
