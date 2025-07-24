package com.prosilion.superconductor.sqlite.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.lang.NonNull;
import org.awaitility.Awaitility;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class TestSubscriber<T> extends BaseSubscriber<T> {
  private final List<T> items = Collections.synchronizedList(new ArrayList<>());
  private final AtomicBoolean completed = new AtomicBoolean(false);
  private Subscription subscription;

  @Override
  public void hookOnSubscribe(@NonNull Subscription subscription) {
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void hookOnNext(@NonNull T value) {
    completed.set(false);
    subscription.request(1);
    completed.set(true);
    items.add(value);
  }

  public List<T> getItems() {
    Awaitility.await()
        .timeout(3, TimeUnit.SECONDS)
        .untilTrue(completed);
    List<T> eventList = List.copyOf(items);
    items.clear();
    return eventList;
  }

  @Override
  protected void hookOnComplete() {
    completed.set(true);
  }
}
