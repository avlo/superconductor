package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.AbstractSubscriberFilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface AbstractSubscriberFilterTypeJoinRepository<T extends AbstractSubscriberFilterType> extends JpaRepository<T, Long> {
  List<T> getAllByFilterId(Long filterId);
}