package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.IdentifierTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierTagEntityRepository<T extends IdentifierTagEntity> extends AbstractTagEntityRepository<T> {
}
