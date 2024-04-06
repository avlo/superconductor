package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventEntityRepository extends JpaRepository<EventEntity, Long> {
  Optional<EventEntity> findByContent(String content);
}