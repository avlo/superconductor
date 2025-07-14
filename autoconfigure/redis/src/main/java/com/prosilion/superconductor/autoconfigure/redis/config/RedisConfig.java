package com.prosilion.superconductor.autoconfigure.redis.config;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableRedisDocumentRepositories
@ComponentScan(
    basePackages = {
        "com.prosilion.superconductor.base.service.clientresponse",
        "com.prosilion.superconductor.base.service.request",
        "com.prosilion.superconductor.base.util",
        "com.prosilion.superconductor.lib.redis.service",
        "com.prosilion.superconductor.autoconfigure.redis.config",
    })
public class RedisConfig {
}
