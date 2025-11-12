package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.CacheKindClassMapService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

public class CacheKindServiceConfig {
  @Bean
  CacheKindClassMapService kindClassMapService(@NonNull List<CacheEventTagBaseEventServiceIF> cacheEventTagBaseEventServiceIFS) {
    return new CacheKindClassMapService(cacheEventTagBaseEventServiceIFS);
  }
}
