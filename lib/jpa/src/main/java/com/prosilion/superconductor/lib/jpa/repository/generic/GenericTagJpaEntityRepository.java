package com.prosilion.superconductor.lib.jpa.repository.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.prosilion.superconductor.lib.jpa.entity.generic.GenericTagJpaEntity;

@Repository
public interface GenericTagJpaEntityRepository extends JpaRepository<GenericTagJpaEntity, Long> {
}
