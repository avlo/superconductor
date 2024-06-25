package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedPubkeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedPubkeyPlugin<
    S extends SubscriberFilterReferencedPubkey, // @MappedSuperclass for below
    T extends SubscriberFilterReferencedPubkeyRepository<S>>
    implements FilterPlugin<S, T> {

  private final SubscriberFilterReferencedPubkeyRepository<S> join;

  @Autowired
  public FilterReferencedPubkeyPlugin(SubscriberFilterReferencedPubkeyRepository<S> join) {
    this.join = join;
  }
  @Override
  public T getAbstractSubscriberFilterTypeJoinRepository() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "referencedPubkey";
  }
}
