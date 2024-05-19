package com.prosilion.superconductor.repository.classified;

import com.prosilion.superconductor.entity.classified.ZapRequestEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZapRequestEventEntityRepository extends JpaRepository<ZapRequestEventEntity, Long> {
}