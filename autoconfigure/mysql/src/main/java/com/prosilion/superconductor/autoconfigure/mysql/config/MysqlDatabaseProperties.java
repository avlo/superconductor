//package com.prosilion.superconductor.autoconfigure.mysql.config;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
///**
// * bean-mapper for external-to-module application.properties file
// * <p>
// * note: this class does not know if application.properties file exists.  that responsibility resides in
// *
// * @EnableConfigurationProperties(H2DatabaseProperties.class) in H2DatabaseAutoConfiguration.java
// */
//@Slf4j
//@ConfigurationProperties(prefix = "spring.datasource")
//@Getter
//@Setter
//public class H2DatabaseProperties {
//  private String url;
//  private String driverClassName;
//  private String username;
//  private String password;
//
//  public H2DatabaseProperties() {
//    log.info("H2DatabaseProperties no-arg Ctor() initialized");
//  }
//
//  public H2DatabaseProperties(String driverClassName, String password, String url, String username) {
//    this.driverClassName = driverClassName;
//    this.password = password;
//    this.url = url;
//    this.username = username;
//    log.info("H2DatabaseProperties full-args Ctor() initialized");
//  }
//}
