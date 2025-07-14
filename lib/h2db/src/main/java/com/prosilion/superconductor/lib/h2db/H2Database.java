package com.prosilion.superconductor.lib.h2db;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * A bean of this class instantiates in either one of two ways:
 * 1) If container doesn't already have one, H2DatabaseAutoConfiguration.java
 * will create one based on selected spring-boot profile
 * 2) Pre-existing by some other mechanism (currently does not occur)
 */
public class H2Database {
  private final H2DatabaseConfigProperties h2DatabaseConfigProperties;

  /**
   * currently, GreetingConfig parameter/object/bean DI/wired exclusively by H2DatabaseAutoConfiguration.java
   */
  public H2Database(H2DatabaseConfigProperties h2DatabaseConfigProperties) {
    this.h2DatabaseConfigProperties = h2DatabaseConfigProperties;
  }

  public DataSource getDataSource() {
    EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
//  TODO: below appears superfluous, causes tests to fail, comment for now and review    
//    String property = h2DatabaseConfigProperties.getProperty(H2DatabaseConfigParams.USERS_DDL);
    EmbeddedDatabaseBuilder b1 = embeddedDatabaseBuilder.setType(EmbeddedDatabaseType.H2);
//  TODO: below appears superfluous, causes tests to fail, comment for now and review    
//    EmbeddedDatabaseBuilder b2 = b1.addScript(property);
    EmbeddedDatabase build = b1.build();
    return build;
  }

//  public DataSource getDataSource() {
//    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//    dataSourceBuilder.url(h2DatabaseConfigProperties.getProperty(H2DatabaseConfigParams.URL));
//    dataSourceBuilder.driverClassName(h2DatabaseConfigProperties.getProperty(H2DatabaseConfigParams.DRIVER_CLASSNAME));
//    dataSourceBuilder.username(h2DatabaseConfigProperties.getProperty(H2DatabaseConfigParams.USERNAME));
//    dataSourceBuilder.password(h2DatabaseConfigProperties.getProperty(H2DatabaseConfigParams.PASSWORD));
//    return dataSourceBuilder.build();
//  }
}
