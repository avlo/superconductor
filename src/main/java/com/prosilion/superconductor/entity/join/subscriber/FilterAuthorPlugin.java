package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterAuthorPlugin<
    S extends SubscriberFilterAuthor, // @MappedSuperclass for below
    T extends SubscriberFilterAuthorRepository<S>>
    implements FilterPlugin<S, T> {

  private final SubscriberFilterAuthorRepository<S> join;

  @Autowired
  public FilterAuthorPlugin(SubscriberFilterAuthorRepository<S> subscriberFilterAuthorRepository) {
    this.join = subscriberFilterAuthorRepository;
  }

  @Override
  public T getAbstractSubscriberFilterTypeJoinRepository() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "author";
  }
}
