package com.prosilion.superconductor.redis.service;

import com.prosilion.superconductor.base.util.FilterMatcher;
import com.prosilion.superconductor.service.BaseFilterMatcherIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class FilterMatcherRedisIT extends BaseFilterMatcherIT {
  @Autowired
  public FilterMatcherRedisIT(FilterMatcher filterMatcher) {
    super(filterMatcher);
  }
}
