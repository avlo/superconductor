package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntityBaseTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EventEntityBaseTagEntityRepository extends JpaRepository<EventEntityBaseTagEntity, Long> {}