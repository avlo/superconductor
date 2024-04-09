package com.prosilion.nostrrelay.pubsub;

import com.prosilion.nostrrelay.entity.Subscriber;

public record AddSubscriberEvent(Subscriber subscriber) {

  public Long getSubscriberId() {
    return subscriber.getId();
  }
}
