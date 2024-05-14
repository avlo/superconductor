package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
  Optional<Subscriber> findBySubscriberId(String subscriberId);
  Optional<List<Subscriber>> findAllBySessionId(String sessionId);
}
