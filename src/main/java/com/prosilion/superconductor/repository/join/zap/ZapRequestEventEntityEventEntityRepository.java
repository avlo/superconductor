package com.prosilion.superconductor.repository.join.zap;

import com.prosilion.superconductor.entity.join.classified.ZapRequestEventEntityEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZapRequestEventEntityEventEntityRepository extends JpaRepository<ZapRequestEventEntityEventEntity, Long> {
}