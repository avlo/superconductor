package com.prosilion.superconductor.pubsub;

import com.prosilion.superconductor.entity.Subscriber;

public record AddSubscriberEvent(Subscriber subscriber) {

  public Long getSubscriberId() {
    return subscriber.getId();
  }
  public String getSubscriberSessionId() {
    return subscriber.getSessionId();
  }
}
