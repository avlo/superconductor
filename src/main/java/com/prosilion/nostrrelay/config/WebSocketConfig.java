package com.prosilion.nostrrelay.config;

import com.prosilion.nostrrelay.controller.NostrEventController;
import com.prosilion.nostrrelay.service.EventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableWebSocket
public class WebSocketConfig {
//  @Bean
//  public ServerEndpointExporter serverEndpoint() {
//    return new ServerEndpointExporter();
//  }
  @Bean
  public NostrEventController nostrEventController(EventService eventService) {
    return new NostrEventController(eventService);
  }
}