package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.ListQueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventNosqlEntityByExampleRepository extends ListCrudRepository<EventNosqlEntity, String>, ListQueryByExampleExecutor<EventNosqlEntity> {
}
