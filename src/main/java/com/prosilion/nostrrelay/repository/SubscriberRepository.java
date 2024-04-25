package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
  Optional<Subscriber> findBySubscriberId(String subscriberId);

  Optional<Subscriber> findBySessionId(String sessionId);

  void deleteBySessionId(String sessionId);

  void deleteBySubscriberId(String subscriberId);
}
