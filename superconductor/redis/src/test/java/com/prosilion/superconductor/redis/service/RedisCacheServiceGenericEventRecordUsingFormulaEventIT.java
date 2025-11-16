package com.prosilion.superconductor.redis.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.TempIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RedisCacheServiceGenericEventRecordUsingFormulaEventIT extends TempIT {
  @Autowired
  public RedisCacheServiceGenericEventRecordUsingFormulaEventIT(
      @Qualifier("eventPlugin") EventPluginIF eventPluginIF,
      CacheEventTagBaseEventServiceIF cacheFormulaEventService) throws ParseException {
    super(eventPluginIF, cacheFormulaEventService);
  }
}
