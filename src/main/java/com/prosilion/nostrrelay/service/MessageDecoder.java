package com.prosilion.nostrrelay.service;

import com.google.gson.Gson;
import com.prosilion.nostrrelay.model.Message;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

  private static Gson gson = new Gson();

  @Override
  public Message decode(String s) throws DecodeException {
    return gson.fromJson(s, Message.class);
  }

  @Override
  public boolean willDecode(String s) {
    return (s != null);
  }

  @Override
  public void init(EndpointConfig endpointConfig) {
    // Custom initialization logic
  }

  @Override
  public void destroy() {
    // Close resources
  }
}