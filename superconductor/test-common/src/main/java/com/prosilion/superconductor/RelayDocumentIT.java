package com.prosilion.superconductor;

import com.prosilion.superconductor.base.util.NostrMediaType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
public abstract class RelayDocumentIT {
  private final String url;
  private final WebTestClient webTestClient;

  public RelayDocumentIT(
      @NonNull WebTestClient webTestClient,
      @NonNull String url) {
    this.webTestClient = webTestClient;
    this.url = url;
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
        .uri(url)
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
