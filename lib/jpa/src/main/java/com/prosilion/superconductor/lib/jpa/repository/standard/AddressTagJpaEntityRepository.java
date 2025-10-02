package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.AddressTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressTagJpaEntityRepository<T extends AddressTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
