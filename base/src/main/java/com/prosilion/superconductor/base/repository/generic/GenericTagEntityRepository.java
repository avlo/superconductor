package com.prosilion.superconductor.base.repository.generic;

import com.prosilion.superconductor.base.entity.generic.GenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenericTagEntityRepository extends JpaRepository<GenericTagEntity, Long> {
}
