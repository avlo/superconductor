//package prosilion.superconductor.lib.jpa.config;
//
//import com.prosilion.superconductor.base.service.event.CacheIF;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.lang.NonNull;
//import prosilion.superconductor.lib.jpa.event.EventEntityService;
//import prosilion.superconductor.lib.jpa.event.JpaCache;
//import prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;
//
//@Configuration
//public class JpaCacheConfig {
//  @Bean
//  @ConditionalOnMissingBean
//  CacheIF cacheIF(
//      @NonNull EventEntityService eventEntityService,
//      @NonNull DeletionEventEntityService deletionEventEntityService) {
//    return new JpaCache(eventEntityService, deletionEventEntityService);
//  }
//}
