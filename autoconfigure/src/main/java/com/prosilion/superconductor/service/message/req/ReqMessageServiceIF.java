package com.prosilion.superconductor.service.message.req;

import com.prosilion.superconductor.service.message.MessageService;
import nostr.event.message.ReqMessage;

public interface ReqMessageServiceIF<T extends ReqMessage> extends MessageService<T> {
}
