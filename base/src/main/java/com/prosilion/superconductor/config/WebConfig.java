package com.prosilion.superconductor.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = com.prosilion.superconductor.base.controller.NostrEventController.class)
public class WebConfig {
}
