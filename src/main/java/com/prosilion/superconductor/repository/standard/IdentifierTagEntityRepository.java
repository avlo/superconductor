package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.IdentifierTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierTagEntityRepository<T extends IdentifierTagEntity> extends AbstractTagEntityRepository<T> {
}