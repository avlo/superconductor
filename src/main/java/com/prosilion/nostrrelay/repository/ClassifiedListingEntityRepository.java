package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassifiedListingEntityRepository extends JpaRepository<ClassifiedListingEntity, Long> {
}