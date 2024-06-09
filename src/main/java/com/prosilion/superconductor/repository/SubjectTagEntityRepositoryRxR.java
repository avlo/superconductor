package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import com.prosilion.superconductor.entity.standard.EventTagEntity;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepository;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectTagEntityRepositoryRxR <T extends SubjectTagEntityRxR> extends StandardTagEntityRepositoryRxR<T> {
  Optional<SubjectTagEntityRxR> findFirstById(Long id);
}