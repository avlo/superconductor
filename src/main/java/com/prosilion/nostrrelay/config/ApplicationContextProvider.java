package com.prosilion.nostrrelay.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
    ApplicationContextProvider.applicationContext = applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }
}