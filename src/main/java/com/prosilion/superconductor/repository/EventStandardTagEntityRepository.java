package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.EventStandardTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStandardTagEntityRepository extends JpaRepository<EventStandardTagEntity, Long> {
}