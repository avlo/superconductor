package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.PubKeyTag;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedPubkeyPlugin<T extends ReferencedPublicKeyFilter<PubKeyTag>> extends AbstractTagFilterPlugin<T, PubKeyTag> {
  public FilterReferencedPubkeyPlugin() {
    super(ReferencedPublicKeyFilter.FILTER_KEY, PubKeyTag.class);
  }
}
