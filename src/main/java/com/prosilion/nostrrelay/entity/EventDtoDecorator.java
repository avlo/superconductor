package com.prosilion.nostrrelay.entity;

import java.lang.reflect.InvocationTargetException;
public interface EventDtoDecorator {
  EventEntityDecorator convertDtoToEntity() throws InvocationTargetException, IllegalAccessException;
}
