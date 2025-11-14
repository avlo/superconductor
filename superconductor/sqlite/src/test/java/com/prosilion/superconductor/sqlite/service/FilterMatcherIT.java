package com.prosilion.superconductor.sqlite.service;

import com.prosilion.superconductor.base.util.FilterMatcher;
import com.prosilion.superconductor.service.BaseFilterMatcherIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class FilterMatcherIT extends BaseFilterMatcherIT {
  @Autowired
  public FilterMatcherIT(FilterMatcher filterMatcher) {
    super(filterMatcher);
  }
}
