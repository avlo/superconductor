package com.prosilion.superconductor.repository.zap;

import com.prosilion.superconductor.entity.zap.ZapRequestEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZapRequestEventEntityRepository extends JpaRepository<ZapRequestEventEntity, Long> {
}