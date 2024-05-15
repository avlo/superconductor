package com.prosilion.superconductor.repository.classified;

import com.prosilion.superconductor.entity.PriceTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PriceTagEntityRepository extends JpaRepository<PriceTagEntity, Long> {
}
