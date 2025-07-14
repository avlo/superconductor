package com.prosilion.superconductor.lib.jpa.repository.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.prosilion.superconductor.lib.jpa.entity.generic.GenericTagEntity;

@Repository
public interface GenericTagEntityRepository extends JpaRepository<GenericTagEntity, Long> {
}
