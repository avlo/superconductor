package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.EventTagEntityJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EventTagEntityRepositoryJoin extends JpaRepository<EventTagEntityJoin, Long> {
}