package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterEventPlugin<
    S extends SubscriberFilterEvent, // @MappedSuperclass for below
    T extends SubscriberFilterEventRepository<S>>
    implements FilterPlugin<S, T> {

  private final SubscriberFilterEventRepository<S> join;

  @Autowired
  public FilterEventPlugin(SubscriberFilterEventRepository<S> subscriberFilterTypeAuthorRepository) {
    this.join = subscriberFilterTypeAuthorRepository;
  }

  @Override
  public T getAbstractSubscriberFilterTypeJoinRepository() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "event";
  }
}
