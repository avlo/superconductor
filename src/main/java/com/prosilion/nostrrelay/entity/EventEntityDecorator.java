package com.prosilion.nostrrelay.entity;

import nostr.base.PublicKey;
import nostr.event.BaseTag;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
public interface EventEntityDecorator {
  EventDtoDecorator convertEntityToDto(PublicKey publicKey, List<BaseTag> baseTags) throws InvocationTargetException, IllegalAccessException;
}
