package com.prosilion.superconductor.sqlite;

import com.prosilion.superconductor.BaseMatchingAddressTagIT;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingAddressTagIT extends BaseMatchingAddressTagIT {
  @Autowired
  MatchingAddressTagIT(@NonNull @Value("${superconductor.relay.url}") String relayUrl) throws IOException {
    super(relayUrl);
  }
}
