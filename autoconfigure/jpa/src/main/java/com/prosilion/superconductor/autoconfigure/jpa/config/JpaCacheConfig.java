package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;
import prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import prosilion.superconductor.lib.jpa.event.EventEntityService;
import prosilion.superconductor.lib.jpa.event.JpaCache;
import prosilion.superconductor.lib.jpa.event.join.generic.GenericTagEntitiesService;
import prosilion.superconductor.lib.jpa.event.join.generic.GenericTagEntityElementAttributeEntityService;
import prosilion.superconductor.lib.jpa.plugin.tag.TagPlugin;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.EventEntityRepository;
import prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventEntityRepository;
import prosilion.superconductor.lib.jpa.repository.generic.ElementAttributeEntityRepository;
import prosilion.superconductor.lib.jpa.repository.generic.GenericTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.generic.EventEntityGenericTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.generic.GenericTagEntityElementAttributeEntityRepository;
import prosilion.superconductor.lib.jpa.service.ConcreteTagEntitiesService;
import prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;

@AutoConfiguration
@EnableJpaRepositories(basePackages = "prosilion.superconductor.lib.jpa.repository")
@EntityScan(basePackages = "prosilion.superconductor.lib.jpa.entity")
@ComponentScan(basePackages = "prosilion.superconductor.lib.jpa.plugin.tag")
@ConditionalOnClass(JpaCache.class)
public class JpaCacheConfig<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>,
    R extends AbstractTagEntity,
    S extends EventEntityAbstractEntity,
    T extends EventEntityAbstractTagEntityRepository<S>> {

  @Bean
  @ConditionalOnMissingBean
  ConcreteTagEntitiesService<P, Q, R, S, T> concreteTagEntitiesService(@NonNull List<TagPlugin<P, Q, R, S, T>> tagPlugins) {
    return new ConcreteTagEntitiesService<>(tagPlugins);
  }

  @Bean
  @ConditionalOnMissingBean
  GenericTagEntityElementAttributeEntityService genericTagEntityElementAttributeEntityService(
      @NonNull ElementAttributeEntityRepository repo,
      @NonNull GenericTagEntityElementAttributeEntityRepository join) {
    return new GenericTagEntityElementAttributeEntityService(repo, join);
  }

  @Bean
  @ConditionalOnMissingBean
  GenericTagEntitiesService genericTagEntitiesService(
      @NonNull GenericTagEntityElementAttributeEntityService genericTagEntityElementAttributeEntityService,
      @NonNull GenericTagEntityRepository genericTagEntityRepository,
      @NonNull EventEntityGenericTagEntityRepository join) {
    return new GenericTagEntitiesService(
        genericTagEntityElementAttributeEntityService,
        genericTagEntityRepository,
        join);
  }

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
  CacheIF cacheIF(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    return new JpaCache(eventEntityService, deletionEventEntityService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }
}
