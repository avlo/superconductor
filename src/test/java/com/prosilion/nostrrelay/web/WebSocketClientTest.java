package com.prosilion.nostrrelay.web;

import jakarta.websocket.*;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Future;

@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
class WebSocketClientTest {
  String WS_LOCALHOST_5555 = "ws://localhost:5555";
  private Session session;
  private TestWebSocketClient testWebSocketClient;

  public static final String QUERY_STRING = "[\"EVENT\",{\"id\":\"d6173464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70\",\"kind\":\"1\",\"content\":\"111111111\",\"pubkey\":\"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\"created_at\":1711354281731,\"tags\":[[\"e\",\"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"],[\"p\",\"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";

  @BeforeEach
  public void setup() throws DeploymentException, IOException {
    testWebSocketClient = new TestWebSocketClient();
    session = ContainerProvider.getWebSocketContainer().connectToServer(testWebSocketClient, URI.create(WS_LOCALHOST_5555));
  }

  @Test
  public void createSessionAfterOpenLogWebSocketHandlerTest() {
    Assertions.assertTrue(session.isOpen());
  }

  @Test
  public void sendTextTest() throws Exception {
    session.getBasicRemote().sendText(QUERY_STRING);
  }

//  @Test
//  public void sendTextTestExpectReturn() throws Exception {
//    session.addMessageHandler(new WebSocketHandlerPartial());
//    Future<Void> returnval = session.getAsyncRemote().sendText(QUERY_STRING);
//
//    System.out.println("666666666666666666666");
//    System.out.println(returnval);
//    System.out.println("666666666666666666666");
//
//
//    String res = testWebSocketClient.execute(
//            URI.create(
//                WS_LOCALHOST_5555
//            ),
//            session -> session.send(
//                    Mono.just(session.textMessage(QUERY_STRING)))
//                .thenMany(session.receive()
//                    .map(WebSocketMessage::getPayloadAsText)
//                    .log())
//                .then())
//        .block(Duration.ofSeconds(5L))
//        .toString();
//    System.out.println("88888888888888888888888888888888");
//    System.out.println(res);
//    System.out.println("88888888888888888888888888888888");
//  }

  private class WebSocketHandlerPartial implements MessageHandler.Partial<String> {
    @Override
    public void onMessage(String s, boolean b) {
      System.out.println("222222222222222222222");
      System.out.println("222222222222222222222");
      //      Assertions.assertTrue(s.contains("RELAY"));
      System.out.println("222222222222222222222");
      System.out.println("222222222222222222222");
    }
  }

  @ClientEndpoint
  private class TestWebSocketClient extends ReactorNettyWebSocketClient {
    @Override
    public Mono<Void> execute(URI url, WebSocketHandler handler) {
      System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA");
      System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA");
      return super.execute(url, handler);
    }

    @Override
    public Mono<Void> execute(URI url, HttpHeaders requestHeaders, WebSocketHandler handler) {
      System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB");
      System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB");
      return super.execute(url, requestHeaders, handler);
    }
  }
}