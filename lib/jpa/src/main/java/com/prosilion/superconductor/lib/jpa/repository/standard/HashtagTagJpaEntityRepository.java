package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.HashtagTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagTagJpaEntityRepository<T extends HashtagTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
}
