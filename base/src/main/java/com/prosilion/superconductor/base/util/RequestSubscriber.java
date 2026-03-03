package com.prosilion.superconductor.base.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.awaitility.Awaitility;
import org.awaitility.core.DurationFactory;
import org.reactivestreams.Subscription;
import org.springframework.lang.NonNull;
import reactor.core.publisher.BaseSubscriber;

public class RequestSubscriber<T> extends BaseSubscriber<T> {
  private final List<T> items = Collections.synchronizedList(new ArrayList<>());
  private final AtomicBoolean completed = new AtomicBoolean(false);
  private Subscription subscription;
  private final Duration timeout;

  public RequestSubscriber() {
    this(DurationFactory.of(3, TimeUnit.SECONDS));
  }

  public RequestSubscriber(Duration timeout) {
    this.timeout = timeout;
  }

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
        .timeout(timeout)
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
