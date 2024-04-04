package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextNoteEventRepository extends JpaRepository<TextNoteEventEntity, Long> {
  Optional<TextNoteEventEntity> findByContent(@NonNull String content);
}