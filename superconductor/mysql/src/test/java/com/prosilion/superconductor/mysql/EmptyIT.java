package com.prosilion.superconductor.mysql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
public class EmptyIT {
  @Test
  void placeholder() {
    log.info("placeholder");
  }
}
