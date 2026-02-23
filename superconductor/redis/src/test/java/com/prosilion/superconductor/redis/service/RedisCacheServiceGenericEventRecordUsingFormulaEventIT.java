package com.prosilion.superconductor.redis.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.service.BaseCacheServiceGenericEventRecordUsingFormulaEventIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class RedisCacheServiceGenericEventRecordUsingFormulaEventIT extends BaseCacheServiceGenericEventRecordUsingFormulaEventIT {
  @Autowired
  public RedisCacheServiceGenericEventRecordUsingFormulaEventIT(
      @NonNull EventPlugin eventPlugin,
      @NonNull Identity superconductorInstanceIdentity) throws ParseException {
    super(eventPlugin, superconductorInstanceIdentity);
  }
}
