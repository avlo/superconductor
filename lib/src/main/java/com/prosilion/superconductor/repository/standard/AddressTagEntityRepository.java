package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.AddressTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressTagEntityRepository<T extends AddressTagEntity> extends AbstractTagEntityRepository<T> {
}
