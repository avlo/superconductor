package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PubkeyTagEntityRepository<T extends PubkeyTagEntity> extends AbstractTagEntityRepository<T> {
  List<T> findByPublicKey(@NonNull String publicKey);
}
