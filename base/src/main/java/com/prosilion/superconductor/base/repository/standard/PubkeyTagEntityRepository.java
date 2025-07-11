package com.prosilion.superconductor.base.repository.standard;

import com.prosilion.superconductor.base.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PubkeyTagEntityRepository<T extends PubkeyTagEntity> extends AbstractTagEntityRepository<T> {
  List<T> findByPublicKey(@NonNull String publicKey);
}
