package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntitySubjectTagEntityRepository extends JpaRepository<EventEntitySubjectTagEntity, Long> {
}