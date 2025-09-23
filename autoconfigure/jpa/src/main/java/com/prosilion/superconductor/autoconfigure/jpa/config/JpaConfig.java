package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.jpa.service.auth.AutoConfigEventMessageServiceAuthDecorator;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.EventEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.ConcreteTagEntitiesService;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;
import com.prosilion.superconductor.lib.jpa.service.EventEntityService;
import com.prosilion.superconductor.lib.jpa.service.GenericTagEntitiesService;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheService;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
  EventEntityService eventEntityService(
      @NonNull ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagEntityRepository<AbstractTagEntity>,
          AbstractTagEntity,
          EventEntityAbstractEntity,
          EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>> concreteTagEntitiesService,
      @NonNull GenericTagEntitiesService genericTagEntitiesService,
      @NonNull EventEntityRepository eventEntityRepository) {
    return new EventEntityService(concreteTagEntitiesService, genericTagEntitiesService, eventEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  DeletionEventEntityService deletionEventEntityService(@NonNull DeletionEventEntityRepository deletionEventEntityRepository) {
    return new DeletionEventEntityService(deletionEventEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  JpaCacheServiceIF cacheIF(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    return new JpaCacheService(eventEntityService, deletionEventEntityService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull JpaCacheServiceIF cacheServiceIF) {
    return new EventPlugin(cacheServiceIF);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
  AutoConfigEventMessageServiceIF getEventMessageServiceAuthDecorator(
      @NonNull EventMessageServiceIF eventMessageService,
      @NonNull AuthEntityServiceIF authEntityServiceIF) {
    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new AutoConfigEventMessageServiceAuthDecorator(eventMessageService, authEntityServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  DataLoaderJpaIF dataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("upvoteBadgeDefinitionEvent") BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull @Qualifier("downvoteBadgeDefinitionEvent") BadgeDefinitionEvent downvoteBadgeDefinitionEvent) {
    return new DataLoaderJpa(eventPlugin, upvoteBadgeDefinitionEvent, downvoteBadgeDefinitionEvent);
  }
}
