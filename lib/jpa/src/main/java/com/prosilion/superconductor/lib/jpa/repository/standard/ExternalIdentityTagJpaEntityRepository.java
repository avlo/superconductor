package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.ExternalIdentityTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalIdentityTagJpaEntityRepository<T extends ExternalIdentityTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
