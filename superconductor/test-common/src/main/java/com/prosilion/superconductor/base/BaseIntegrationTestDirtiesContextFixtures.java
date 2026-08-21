package com.prosilion.superconductor.base;

import com.prosilion.nostr.user.Identity;
import lombok.NonNull;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTestDirtiesContextFixtures extends BaseTestFixtures {
  public BaseIntegrationTestDirtiesContextFixtures(@NonNull Identity superconductorInstanceIdentity) {
    super(superconductorInstanceIdentity);
  }
}
