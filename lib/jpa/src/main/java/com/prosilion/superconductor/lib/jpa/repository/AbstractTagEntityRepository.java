package com.prosilion.superconductor.lib.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;

@NoRepositoryBean
public interface AbstractTagEntityRepository<T extends AbstractTagEntity> extends JpaRepository<T, Long> {
}
