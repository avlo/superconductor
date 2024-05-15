package com.prosilion.superconductor.repository.join.classified;

import com.prosilion.superconductor.entity.join.classified.ClassifiedListingEntityEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassifiedListingEntityEventEntityRepository extends JpaRepository<ClassifiedListingEntityEventEntity, Long> {}