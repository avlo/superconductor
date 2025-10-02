package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.IdentifierTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierTagJpaEntityRepository<T extends IdentifierTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
