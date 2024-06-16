package com.prosilion.superconductor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootTest
class KeepForWebClientReference {
  private final static String SCHEME_HTTPS = "https";
  private final static String SCHEME_HTTP = "http";
  private final static String SCHEME_WS = "ws";
  private final static String HOST = "localhost";
  private final static String PORT = "5555";
  private final static String BASE_URI = "";
  private final static String REQ_URI = "REQ.html";
  private WebClient webClient;

  private final static String requestJson = "[\"REQ\",\"65c7dc9c25d6d2f3c993a4b6ae8ff0a4eb95626319ec3462b41c088e102fd3b1\",{\"ids\":[\"1111111111\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";

  @BeforeEach
  void setup() {
    webClient = WebClient.builder()
        .filter(
            logRequest())
        .build();
  }

  @Test
  void testRelayDocument() {
    String response = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .scheme(SCHEME_WS)
            .host(HOST)
            .port(PORT)
            .path(REQ_URI)
            .build())
        .accept(MediaType.TEXT_PLAIN)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    System.out.println("11111111111111");
    System.out.println("11111111111111");
    System.out.println(response);
    System.out.println("11111111111111");
    System.out.println("11111111111111");
//    return new ObjectMapper().readValue(entityChopped, returnType);

  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      log.info(String.format("Request: %s %s", clientRequest.method(), clientRequest.url()));
      clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(String.format("%s=%s", name, value))));
      return Mono.just(clientRequest);
    });
  }
}
