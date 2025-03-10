package com.prosilion.superconductor.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseMessage;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

@Slf4j
public class StandardWebSocketClient extends TextWebSocketHandler {
  @Getter
  private final WebSocketSession clientSession;
  private final AtomicBoolean completed = new AtomicBoolean(false);

  @Getter
  private final List<String> events = Collections.synchronizedList(new ArrayList<>());

  public StandardWebSocketClient(@NonNull String relayUri) throws ExecutionException, InterruptedException {
    org.springframework.web.socket.client.standard.StandardWebSocketClient standardWebSocketClient = new org.springframework.web.socket.client.standard.StandardWebSocketClient();
    this.clientSession = getClientSession(relayUri, standardWebSocketClient);
    log.debug("Non-Secure (WS) WebSocket client connected {}", clientSession.getId());
  }

  public StandardWebSocketClient(@NonNull String relayUri, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
    org.springframework.web.socket.client.standard.StandardWebSocketClient standardWebSocketClient = new org.springframework.web.socket.client.standard.StandardWebSocketClient();
    standardWebSocketClient.setSslContext(sslBundles.getBundle("server").createSslContext());
    this.clientSession = getClientSession(relayUri, standardWebSocketClient);
    log.debug("Secure (WSS) WebSocket client connected {}", clientSession.getId());
  }

  private WebSocketSession getClientSession(@NonNull String relayUri, org.springframework.web.socket.client.standard.StandardWebSocketClient standardWebSocketClient) throws InterruptedException, ExecutionException {
    return standardWebSocketClient
        .execute(
            this,
            new WebSocketHttpHeaders(),
            URI.create(relayUri))
        .get();
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    String payload = message.getPayload();
    log.debug("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
    log.debug("socket:\n  [{}]\n", session.getId());
    log.debug("------------------------------");
    log.debug("  " + payload);
    log.debug("------------------------------");
    log.debug("events BEFORE payload:");
    log.debug(events.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    log.debug("------------------------------");
    events.add(payload);
    log.debug("events AFTER  payload:");
    log.debug(events.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    log.debug("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ\n\n");
    completed.setRelease(true);
  }

  public <T extends BaseMessage> void send(T eventMessage) throws IOException {
    send(eventMessage.encode());
  }

  public void send(String json) throws IOException {
    clientSession.sendMessage(new TextMessage(json));
    await()
        .timeout(66, TimeUnit.MINUTES)
        .untilTrue(completed);
    completed.setRelease(false);
  }
}
