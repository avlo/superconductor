package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.AbstractTagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractTagEntityRepository<T extends AbstractTagEntity> extends CrudRepository<T, Long> {
}