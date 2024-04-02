package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.entity.BaseTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseTagRepository extends JpaRepository<BaseTagEntity, Long> {
}