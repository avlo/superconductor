package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.SubjectTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagEntityRepository<T extends SubjectTagEntity> extends AbstractTagEntityRepository<T> {
}
