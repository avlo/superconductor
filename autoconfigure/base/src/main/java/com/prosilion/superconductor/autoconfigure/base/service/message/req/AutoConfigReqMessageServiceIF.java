package com.prosilion.superconductor.autoconfigure.base.service.message.req;

import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.base.service.message.MessageServiceIF;

public interface AutoConfigReqMessageServiceIF<T extends ReqMessage> extends MessageServiceIF<T> {
}
