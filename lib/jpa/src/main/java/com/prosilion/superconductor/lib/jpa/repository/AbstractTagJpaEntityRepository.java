package com.prosilion.superconductor.lib.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;

@NoRepositoryBean
public interface AbstractTagJpaEntityRepository<T extends AbstractTagJpaEntity> extends JpaRepository<T, Long> {
}
