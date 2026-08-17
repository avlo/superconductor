package java.com.prosilion.superconductor.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.com.prosilion.superconductor.config.SingleContainerCuratedTestConfig;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@Import(SingleContainerCuratedTestConfig.class)
public class CacheCuratedFormulaEventMessageSupplierRemoteIT extends AbstractCacheCuratedFormulaEventMessageSupplierRemoteIT {
  @Autowired
  CacheCuratedFormulaEventMessageSupplierRemoteIT(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull @Value("${superconductor.relay.url.three}") String superconductorRelayUrlThree) throws NostrException {
    super(cacheServiceIF, superconductorInstanceIdentity, superconductorRelayUrlThree);
  }
}
