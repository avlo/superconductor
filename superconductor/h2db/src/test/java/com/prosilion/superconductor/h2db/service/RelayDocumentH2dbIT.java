package com.prosilion.superconductor.h2db.service;

import com.prosilion.superconductor.RelayDocumentIT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class RelayDocumentH2dbIT extends RelayDocumentIT {
  public static final String HTTP_LOCALHOST_5555 = "http://localhost:5555/";

  @Autowired
  RelayDocumentH2dbIT(@NonNull ClientHttpConnector clientHttpConnector) {
    super(WebTestClient.bindToServer(clientHttpConnector).build(), HTTP_LOCALHOST_5555);
  }
}
