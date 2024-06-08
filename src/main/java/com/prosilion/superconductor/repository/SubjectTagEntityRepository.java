package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectTagEntityRepository extends JpaRepository<SubjectTagEntity, Long> {
  Optional<SubjectTagEntity> findFirstById(Long id);
}