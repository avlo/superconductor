package com.prosilion.nostrrelay.repository.join;

import com.prosilion.nostrrelay.entity.join.ClassifiedListingEventTagEntityJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ClassifiedListingEventTagEntityRepository extends JpaRepository<ClassifiedListingEventTagEntityJoin, Long> {
}