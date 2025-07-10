package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrMediaType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class RelayDocumentIT {
  public static final String HTTP_LOCALHOST_5555 = "http://localhost:5555/";
  @Autowired
  ClientHttpConnector clientHttpConnector;
  WebTestClient webTestClient;
  WebClient webClient;

  RelayDocumentIT() {
    webTestClient = WebTestClient.bindToServer(clientHttpConnector).build();
    webClient = WebClient.create();
  }

//  @Test
//  void testRelayDocumentReturnsBody() {
//    webTestClient.get()
//        .uri(HTTP_LOCALHOST_5555)
//        .accept(MediaType.asMediaType(NostrMediaType.APPLICATION_NOSTR_JSON))
//        .exchange()
//        .expectBody();
//  }
//
//  @Test
//  void testRelayDocumentReturnsBody2() {
//    webTestClient.get()
//        .uri(HTTP_LOCALHOST_5555)
//        .accept(MediaType.asMediaType(NostrMediaType.APPLICATION_NOSTR_JSON))
//        .exchange()
//        .expectBody();
//  }

  @Test
  void testRelayDocumentReturnsOk() {
    webTestClient.get()
        .uri(HTTP_LOCALHOST_5555)
        .accept(
            MediaType.asMediaType(NostrMediaType.APPLICATION_NOSTR_JSON));

//    ResponseSpec exchange = accept.exchange();
//        .exchange
//    HeaderAssertions headerAssertions = exchange.expectHeader();
//    System.out.println("1111111111111111");
//    System.out.println(exchange.fla);
//    System.out.println(headerAssertions.valueMatches("Upgrade", "websocket"));
  }
}
