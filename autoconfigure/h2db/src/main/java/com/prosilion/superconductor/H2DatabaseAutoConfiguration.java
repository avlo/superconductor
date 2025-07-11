package com.prosilion.superconductor;

import com.prosilion.superconductor.h2db.H2Database;
import com.prosilion.superconductor.h2db.H2DatabaseConfigParams;
import com.prosilion.superconductor.h2db.H2DatabaseConfigProperties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(H2Database.class)

/**
 *  H2DatabaseProperties bean created (and autowired into this class) with its fields in either one of two states:
 *    1) fields properly populated iff:
 *      a) an application.properties file exists
 *      b) requisite fields are defined within the file
 *  otherwise:
 *    2) field values set to null
 */
@EnableConfigurationProperties(H2DatabaseProperties.class)
public class H2DatabaseAutoConfiguration {
  private static final String DEFAULT_DB = "jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=TRUE";
  private static final String DEFAULT_DRIVER_CLASSNAME = "org.h2.Driver";
  private static final String DEFAULT_USERNAME = "h2dbuser";
  private static final String DEFAULT_PASSWORD = "h2dbuserpass";

  private final H2DatabaseProperties dbProps;

  @Autowired
  public H2DatabaseAutoConfiguration(H2DatabaseProperties h2DatabaseProperties) {
    this.dbProps = h2DatabaseProperties;
  }

  /**
   * H2DatabaseConfigProperties bean manually created here iff user-defined H2DatabaseConfigProperties bean does not exist.
   * <p>
   * furthermore, this H2DatabaseConfigProperties instances field values are populated in either one of two ways:
   * 1) via H2DatabaseProperties bean non-null fields (handled by @EnableConfigurationProperties(H2DatabaseProperties.class), above)
   * otherwise, if any H2DatabaseProperties bean fields are null:
   * 2) set explicit/default string values as seen below
   * <p>
   * careful/note: any H2DatabaseConfigProperties bean with any/all fields pre-populated can only be done via EXTERNAL
   * application.properties file.  i.e., the "h2db-spring-boot-sample-app" application.properties file is EXTERNAL
   */
  @Bean
  @ConditionalOnMissingBean
  public H2DatabaseConfigProperties h2DatabaseConfigProperties() {
    H2DatabaseConfigProperties h2DatabaseConfigProperties = new H2DatabaseConfigProperties();
    h2DatabaseConfigProperties.put(H2DatabaseConfigParams.URL, go(dbProps.getUrl(), DEFAULT_DB));
    h2DatabaseConfigProperties.put(H2DatabaseConfigParams.DRIVER_CLASSNAME, go(dbProps.getDriverClassName(), DEFAULT_DRIVER_CLASSNAME));
    h2DatabaseConfigProperties.put(H2DatabaseConfigParams.USERNAME, go(dbProps.getUsername(), DEFAULT_USERNAME));
    h2DatabaseConfigProperties.put(H2DatabaseConfigParams.PASSWORD, go(dbProps.getPassword(), DEFAULT_PASSWORD));
    return h2DatabaseConfigProperties;
  }

  private String go(String a, String b) {
    return StringUtils.isBlank(a) ? b : a;
  }

  @Bean
  @ConditionalOnMissingBean
  public H2Database h2Database(H2DatabaseConfigProperties h2DatabaseConfigProperties) {
    return new H2Database(h2DatabaseConfigProperties);
  }

  @Bean
  @Primary
  public DataSource getDataSource(H2Database h2Database) {
    return h2Database.getDataSource();
  }
}
