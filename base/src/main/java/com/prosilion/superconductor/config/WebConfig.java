package com.prosilion.superconductor.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(
    basePackageClasses = com.prosilion.superconductor.base.controller.NostrEventController.class,
    basePackages = {
        "com.prosilion.superconductor.base.plugin.filter"
    })
public class WebConfig {
}
