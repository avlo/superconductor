package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.nostr.event.BadgeAwardDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.EventJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.ConcreteTagEntitiesService;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.EventJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.GenericTagJpaEntitiesService;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheService;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

@AutoConfiguration
@EnableJpaRepositories(
    basePackages = {
        "com.prosilion.superconductor.lib.jpa.repository"
    })
@EntityScan(
    basePackages = {
        "com.prosilion.superconductor.lib.jpa.entity"
    })
@ComponentScan(
    basePackages = {
        "com.prosilion.superconductor.lib.jpa.repository",
        "com.prosilion.superconductor.base.service.clientresponse",
        "com.prosilion.superconductor.base.service.request",
        "com.prosilion.superconductor.base.util",
        "com.prosilion.superconductor.lib.jpa.plugin.tag",
        "com.prosilion.superconductor.lib.jpa.service"
    })
@ConditionalOnClass(JpaCacheService.class)
@Slf4j
public class JpaConfig {

  @Bean
  @ConditionalOnMissingBean
  EventJpaEntityService eventEntityService(
      @NonNull ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagJpaEntityRepository<AbstractTagJpaEntity>,
          AbstractTagJpaEntity,
          EventEntityAbstractJpaEntity,
          EventEntityAbstractTagJpaEntityRepository<EventEntityAbstractJpaEntity>> concreteTagEntitiesService,
      @NonNull GenericTagJpaEntitiesService genericTagJpaEntitiesService,
      @NonNull EventJpaEntityRepository eventJpaEntityRepository) {
    return new EventJpaEntityService(concreteTagEntitiesService, genericTagJpaEntitiesService, eventJpaEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  DeletionEventJpaEntityService deletionEventEntityService(@NonNull DeletionEventJpaEntityRepository deletionEventJpaEntityRepository) {
    return new DeletionEventJpaEntityService(deletionEventJpaEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  JpaCacheServiceIF cacheIF(
      @NonNull EventJpaEntityService eventJpaEntityService,
      @NonNull DeletionEventJpaEntityService deletionEventJpaEntityService) {
    return new JpaCacheService(eventJpaEntityService, deletionEventJpaEntityService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull JpaCacheServiceIF cacheServiceIF) {
    return new EventPlugin(cacheServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  DataLoaderJpaIF dataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("upvoteBadgeDefinitionEvent") BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull @Qualifier("downvoteBadgeDefinitionEvent") BadgeAwardDefinitionEvent downvoteBadgeDefinitionEvent) {
    return new DataLoaderJpa(eventPlugin, upvoteBadgeDefinitionEvent, downvoteBadgeDefinitionEvent);
  }
}
