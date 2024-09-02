package com.prosilion.superconductor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = {"/event.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@TestPropertySource("/application-test.properties")
class WebSocketClientRxRIT {
  WebSocketClient client;
  Sinks.Many<String> sendBuffer;
  Sinks.Many<String> receiveBuffer;
  Disposable subscription;

  String relayUrl;

  String expectedMatch = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  String expectedNotMatch = "6f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  String reqJson = "[\"REQ\",\"WebSocketClientIT\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"]}]";

  public WebSocketClientRxRIT(@Value("${superconductor.relay.url}") String relayUrl) {
    this.relayUrl = relayUrl;
    this.client = new ReactorNettyWebSocketClient();
  }

  @BeforeAll
  void setup() {
    connect();
  }

  @Test
  void clientSendAndReceiveTest() {
    Mono
        .fromRunnable(
            () -> send(reqJson))
        .thenMany(
            receive())
        .doOnNext(
            message -> {
              assertFalse(message.contains(expectedMatch));
              assertFalse(message.contains(expectedNotMatch));
            })
        .subscribe();
  }

  private void connect() {
    sendBuffer = Sinks.many().unicast().onBackpressureBuffer();
    receiveBuffer = Sinks.many().unicast().onBackpressureBuffer();
    subscription = client
        .execute(URI.create(relayUrl), this::handleSession)
        .subscribe();
  }

  private void send(String message) {
    sendBuffer.tryEmitNext(message);
  }

  private Flux<String> receive() {
    return receiveBuffer.asFlux();
  }

  private Mono<Void> handleSession(WebSocketSession session) {
    Mono<Void> input = session
        .receive()
        .map(WebSocketMessage::getPayloadAsText)
        .doOnNext(receiveBuffer::tryEmitNext)
        .then();

    Mono<Void> output = session
        .send(
            sendBuffer
                .asFlux()
                .map(session::textMessage)
        );

    return Mono.zip(input, output)
        .then();
  }

  @AfterAll
  public void disconnect() {
    if (subscription != null && !subscription.isDisposed()) {
      subscription.dispose();
      subscription = null;
    }
  }
}
