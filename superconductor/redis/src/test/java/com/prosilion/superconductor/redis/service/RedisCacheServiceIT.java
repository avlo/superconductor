package com.prosilion.superconductor.redis.service;

import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.service.BaseCacheServiceIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class RedisCacheServiceIT extends BaseCacheServiceIT {
  @Autowired
  public RedisCacheServiceIT(CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }
}
