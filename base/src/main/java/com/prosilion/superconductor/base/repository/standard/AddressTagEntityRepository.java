package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.AddressTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressTagEntityRepository<T extends AddressTagEntity> extends AbstractTagEntityRepository<T> {
}
