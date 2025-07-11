package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.ReferenceTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceTagEntityRepository<T extends ReferenceTagEntity> extends AbstractTagEntityRepository<T> {
}
