//package com.prosilion.superconductor.service.event;
//
//import com.prosilion.superconductor.entity.auth.AuthEntity;
//import com.prosilion.superconductor.repository.auth.AuthEntityRepository;
//import org.springframework.lang.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Slf4j
//@Service
//@ConditionalOnProperty(
//    name = "superconductor.auth.active",
//    havingValue = "true")
//public class AuthEntityService {
//  private final AuthEntityRepository authEntityRepository;
//
//  public AuthEntityService(AuthEntityRepository authEntityRepository) {
//    this.authEntityRepository = authEntityRepository;
//  }
//
//  public Long save(AuthEntity authEntity) {
//    removeAuthEntityBySessionId(authEntity.getSessionId());
//    return authEntityRepository.save(authEntity).getId();
//  }
//
//  public Optional<AuthEntity> findAuthEntityBySessionId(@NonNull String sessionId) {
//    return authEntityRepository.findBySessionId(sessionId);
//  }
//
//  public void removeAuthEntityBySessionId(@NonNull String sessionId) {
//    findAuthEntityBySessionId(sessionId).ifPresent(authEntityRepository::delete);
//  }
//}
