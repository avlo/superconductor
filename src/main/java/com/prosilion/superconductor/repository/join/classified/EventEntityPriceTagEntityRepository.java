package com.prosilion.superconductor.repository.join.classified;

import com.prosilion.superconductor.entity.join.classified.EventEntityPriceTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPriceTagEntityRepository extends JpaRepository<EventEntityPriceTagEntity, Long> {}