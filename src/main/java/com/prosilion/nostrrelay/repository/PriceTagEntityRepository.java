package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.PriceTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PriceTagEntityRepository extends JpaRepository<PriceTagEntity, Long> {
}
