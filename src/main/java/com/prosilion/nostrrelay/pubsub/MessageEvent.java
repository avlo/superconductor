package com.prosilion.nostrrelay.pubsub;

public interface MessageEvent {
  String getMessage();

  String getSessionId();
}
