package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<TextNoteEventEntity, Long> {
  TextNoteEventEntity save(TextNoteEventEntity t);

  TextNoteEventEntity findByContent(String content);
}