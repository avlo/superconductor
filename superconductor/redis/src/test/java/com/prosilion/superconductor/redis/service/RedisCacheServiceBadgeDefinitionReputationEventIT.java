//package com.prosilion.superconductor.redis.service;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
//import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
//import com.prosilion.superconductor.service.BaseCacheServiceGenericEventRecordUsingFormulaEventIT;
//import com.prosilion.superconductor.service.BaseCacheServiceGenericEventRecordUsingReputationDefinitionEventIT;
//import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.lang.NonNull;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest
//@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
//public class RedisCacheServiceBadgeDefinitionReputationEventIT extends BaseCacheServiceGenericEventRecordUsingReputationDefinitionEventIT {
//  @Autowired
//  public RedisCacheServiceBadgeDefinitionReputationEventIT(
//      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPluginIF,
//      @NonNull CacheEventTagBaseEventServiceIF cacheFormulaEventService) throws ParseException {
//    super(eventPluginIF, cacheFormulaEventService);
//  }
//}
