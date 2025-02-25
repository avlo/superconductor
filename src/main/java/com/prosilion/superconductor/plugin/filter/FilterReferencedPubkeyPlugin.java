package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.ReferencedPublicKeyFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.PubKeyTag;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedPubkeyPlugin<T extends ReferencedPublicKeyFilter<PubKeyTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, PubKeyTag> {

  public FilterReferencedPubkeyPlugin() {
    super(PubKeyTag.class);
  }

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
