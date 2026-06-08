package com.prosilion.superconductor.lib.jpa.repository.standard;

import com.prosilion.superconductor.lib.jpa.entity.standard.PubkeyTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PubkeyTagJpaEntityRepository<T extends PubkeyTagJpaEntity> extends AbstractTagJpaEntityRepository<T> {
  List<T> findByPublicKey(@NonNull String publicKey);
}
