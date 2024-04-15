package com.prosilion.nostrrelay.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriberNotifier {

  @EventListener
  public void eventOccurred() {
  }
}
