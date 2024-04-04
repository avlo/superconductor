package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.ClassifiedListingEventEntity;
import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassifiedListingEventRepository extends JpaRepository<ClassifiedListingEventEntity, Long> {
  Optional<ClassifiedListingEventEntity> findByContent(@NonNull String content);
}