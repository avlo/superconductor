package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.ClassifiedListingEventEntity;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
@Repository
public interface ClassifiedListingEventRepositoryJoin extends JpaRepository<ClassifiedListingEventEntity, Long> {
  @Transactional
  @NotNull
  ClassifiedListingEventEntity save(@NonNull ClassifiedListingEventEntity t);
}