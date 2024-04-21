package com.prosilion.nostrrelay.web;

import com.prosilion.nostrrelay.controller.RelayInfoDocController;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
class WebClientTest {
  public final static String WS_LOCALHOST_5555 = "ws://localhost:5555";

  @Test
  public void sesseionConnectionTest() {
    String returnval = WebClient.builder()
        .filter(logRequest())
        .build().get()
        .uri(WS_LOCALHOST_5555)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    assertTrue(returnval.contains(RelayInfoDocController.DESCRIPTION));
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      log.info(String.format("Request: %s %s", clientRequest.method(), clientRequest.url()));
      clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(value)));
      return Mono.just(clientRequest);
    });
  }
}