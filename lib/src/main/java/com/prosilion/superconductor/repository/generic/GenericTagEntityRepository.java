package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenericTagEntityRepository extends JpaRepository<GenericTagEntity, Long> {
}