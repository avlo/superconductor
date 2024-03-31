package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<TextNoteEventEntity, Long> {
  @Transactional
  @NotNull
  TextNoteEventEntity save(@NonNull TextNoteEventEntity t);

  Optional<TextNoteEventEntity> findByContent(@NonNull String content);
}