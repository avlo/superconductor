package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
  Optional<Subscriber> findBySubscriberId(String subscriberId);

  Optional<List<Subscriber>> findBySessionId(String sessionId);
}
