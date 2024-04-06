package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.EventEntityPriceTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EventEntityPriceTagEntityRepository extends JpaRepository<EventEntityPriceTagEntity, Long> {
}