package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntityPriceTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPriceTagEntityRepository extends JpaRepository<EventEntityPriceTagEntity, Long> {}