//package com.prosilion.superconductor.supplier.remote;
//
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.base.cache.CacheServiceIF;
//import com.prosilion.superconductor.config.SingleContainerCuratedTestConfig;
//import com.prosilion.superconductor.supplier.remote.abstracts.AbstractCacheBadgeAwardCanonicalEventMessageSupplierRemoteListIT;
//import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//@Import(SingleContainerCuratedTestConfig.class)
//@TestPropertySource(properties = {
//   "superconductor.event.curation.active=false"
//})
//public class CacheBadgeAwardCanonicalEventMessageSupplierRemoteListIT extends AbstractCacheBadgeAwardCanonicalEventMessageSupplierRemoteListIT {
//  @Autowired
//  CacheBadgeAwardCanonicalEventMessageSupplierRemoteListIT(
//     @NonNull Identity superconductorInstanceIdentity,
//     @NonNull @Value("${superconductor.relay.url.two}") String superconductorRelayUrlTwo,
//     @NonNull @Value("${superconductor.relay.url.three}") String superconductorRelayUrlThree,
//     CacheServiceIF cacheServiceIF) throws NostrException {
//    super(superconductorInstanceIdentity, superconductorRelayUrlTwo, superconductorRelayUrlThree, cacheServiceIF);
//  }
//}
