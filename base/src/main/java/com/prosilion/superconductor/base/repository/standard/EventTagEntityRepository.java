package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.EventTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTagEntityRepository<T extends EventTagEntity> extends AbstractTagEntityRepository<T> {
}
