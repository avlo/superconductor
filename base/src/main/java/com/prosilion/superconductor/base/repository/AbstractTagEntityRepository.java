package com.prosilion.superconductor.base.repository;

import com.prosilion.superconductor.base.entity.AbstractTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractTagEntityRepository<T extends AbstractTagEntity> extends JpaRepository<T, Long> {
}
