package com.prosilion.nostrrelay.web;

import jakarta.websocket.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * NOTE: this class/tests currently require an actively running web server
 */
class WebSocketClientTest {
  public final static String WS_LOCALHOST_5555 = "ws://localhost:5555";
  private WebSocketContainer container;

  private String queryString = "[\"EVENT\",{\"id\":\"d6173464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70\",\"kind\":\"1\",\"content\":\"111111111\",\"pubkey\":\"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\"created_at\":1711354281731,\"tags\":[[\"e\",\"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"],[\"p\",\"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";

  /**
   * NOTE: this class/tests currently require an actively running web server
   */
  @BeforeEach
  public void setup() {
    container = ContainerProvider.getWebSocketContainer();
  }

  @Test
  public void createSessionAfterOpenLogWebSocketHandler() throws Exception {
    TestWebSocketClient client = new TestWebSocketClient();
    Session session = container.connectToServer(client, URI.create(WS_LOCALHOST_5555));
    Assertions.assertTrue(session.isOpen());

    session.getBasicRemote().sendText(queryString);
  }

  @ClientEndpoint
  public class TestWebSocketClient {
    Session session;

    @OnOpen
    public void onOpen(final Session session) {
      this.session = session;
      System.out.println("11111111111111111111");
      System.out.println("11111111111111111111");
      this.session.addMessageHandler(new MyMessageHandler());
      System.out.println("11111111111111111111");
      System.out.println("11111111111111111111");
    }
  }

  public class MyMessageHandler implements MessageHandler.Whole<String>, MessageHandler.Partial<String> {

    @Override
    public void onMessage(String s) {
      System.out.println("222222222222222222222222");
    }

    @Override
    public void onMessage(String o, boolean b) {
      System.out.println("3333333333333333333");
    }
  }
}