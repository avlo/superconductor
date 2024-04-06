package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.ClassifiedListingEntityEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ClassifiedListingEntityEventEntityRepository extends JpaRepository<ClassifiedListingEntityEventEntity, Long> {
}