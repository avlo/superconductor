package com.prosilion.superconductor.service.message.req;

import com.prosilion.superconductor.service.message.MessageServiceIF;
import nostr.event.message.ReqMessage;

public interface AutoConfigReqMessageServiceIF<T extends ReqMessage> extends MessageServiceIF<T> {
}
