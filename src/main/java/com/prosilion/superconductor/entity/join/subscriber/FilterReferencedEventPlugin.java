package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedEventPlugin<
    S extends SubscriberFilterReferencedEvent, // @MappedSuperclass for below
    T extends SubscriberFilterReferencedEventRepository<S>>
    implements FilterPlugin<S, T> {

  private final SubscriberFilterReferencedEventRepository<S> join;

  @Autowired
  public FilterReferencedEventPlugin(SubscriberFilterReferencedEventRepository<S> join) {
    this.join = join;
  }
  @Override
  public T getAbstractSubscriberFilterTypeJoinRepository() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "referencedEvent";
  }
}
