package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.IdentifierTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierTagEntityRepository<T extends IdentifierTagEntity> extends AbstractTagEntityRepository<T> {
}
