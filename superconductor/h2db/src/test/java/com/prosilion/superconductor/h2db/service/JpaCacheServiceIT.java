package com.prosilion.superconductor.h2db.service;

import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.service.BaseCacheServiceIT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class JpaCacheServiceIT extends BaseCacheServiceIT {
  @Autowired
  public JpaCacheServiceIT(CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }
}
