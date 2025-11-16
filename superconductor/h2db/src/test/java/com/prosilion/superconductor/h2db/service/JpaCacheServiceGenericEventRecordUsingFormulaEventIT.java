package com.prosilion.superconductor.h2db.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.TempIT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class JpaCacheServiceGenericEventRecordUsingFormulaEventIT extends TempIT {
  @Autowired
  public JpaCacheServiceGenericEventRecordUsingFormulaEventIT(
      @Qualifier("eventPlugin") EventPluginIF eventPluginIF,
      CacheEventTagBaseEventServiceIF cacheFormulaEventService) throws ParseException {
    super(eventPluginIF, cacheFormulaEventService);
  }
}
