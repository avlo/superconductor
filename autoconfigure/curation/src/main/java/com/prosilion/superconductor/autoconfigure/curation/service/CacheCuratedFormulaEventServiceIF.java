package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedFormulaEventServiceIF extends
   CacheCuratedAbstractEventServiceIF<
      CuratedFormulaEvent,
      FormulaEvent,
      AddressTag> {
  Optional<CuratedFormulaEvent> getByAuthorAndIdentifierTag(@NonNull PublicKey author, @NonNull IdentifierTag identifierTag);
  Optional<CuratedFormulaEvent> getByDirect(@NonNull EventTag eventTag);
}
