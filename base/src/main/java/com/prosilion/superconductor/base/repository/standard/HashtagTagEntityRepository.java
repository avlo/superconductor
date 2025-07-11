package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.HashtagTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagTagEntityRepository<T extends HashtagTagEntity> extends AbstractTagEntityRepository<T> {
}
