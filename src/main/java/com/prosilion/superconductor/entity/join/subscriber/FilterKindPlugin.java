package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterKindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterKindPlugin<
    S extends SubscriberFilterKind, // @MappedSuperclass for below
    T extends SubscriberFilterKindRepository<S>>
    implements FilterPlugin<S, T> {

  private final SubscriberFilterKindRepository<S> join;

  @Autowired
  public FilterKindPlugin(SubscriberFilterKindRepository<S> join) {
    this.join = join;
  }

  @Override
  public T getAbstractSubscriberFilterTypeJoinRepository() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "kind";
  }
}
