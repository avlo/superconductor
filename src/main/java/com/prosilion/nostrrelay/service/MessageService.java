package com.prosilion.nostrrelay.service;

import nostr.event.message.EventMessage;

public interface MessageService {
  EventMessage getMessage(EventMessage message);
}
