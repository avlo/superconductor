package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.PubKeyTag;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedPubkeyPlugin extends AbstractTagFilterPlugin<PubKeyTag> {
  public FilterReferencedPubkeyPlugin() {
    super(ReferencedPublicKeyFilter.FILTER_KEY, PubKeyTag.class);
  }
}
