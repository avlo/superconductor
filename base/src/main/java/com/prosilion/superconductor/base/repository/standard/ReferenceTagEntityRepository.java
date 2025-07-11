package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.ReferenceTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceTagEntityRepository<T extends ReferenceTagEntity> extends AbstractTagEntityRepository<T> {
}
