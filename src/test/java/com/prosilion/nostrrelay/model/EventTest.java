package com.prosilion.nostrrelay.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
  @Test
  void nonNullParametersTest() {
    assertDoesNotThrow(() ->
        new Event("id", "pubkey", 1L, 1, new ArrayList<Tag>(), "content", "sig"));
  }

  @Test
  void nullIdThrowsException() {
    assertThrows(NullPointerException.class, () ->
        new Event(null, "pubkey", 1L, 1, new ArrayList<Tag>(), "content", "sig"));
  }

  @Test
  void nullPubkeyThrowsException() {
    assertThrows(NullPointerException.class, () ->
        new Event("id", null, 1L, 1, new ArrayList<Tag>(), "content", "sig"));
  }

//  @Test
//  void nonNullCreatedAtThrowsException() {
//    assertDoesNotThrow(() ->
//        new Event("id", "pubkey", null, 1, new ArrayList<Tag>(), "content", "sig"));
//  }
//
//  @Test
//  void nonNullKindThrowsException() {
//    assertDoesNotThrow(() ->
//        new Event("id", "pubkey", 1L, null, new ArrayList<Tag>(), "content", "sig"));
//  }

  @Test
  void nonNullTagsThrowsException() {
    assertThrows(NullPointerException.class, () ->
        new Event("id", "pubkey", 1L, 1, null, "content", "sig"));
  }

  @Test
  void nonNullContentThrowsException() {
    assertThrows(NullPointerException.class, () ->
        new Event("id", "pubkey", 1L, 1, new ArrayList<Tag>(), null, "sig"));
  }

  @Test
  void nonNullSigThrowsException() {
    assertThrows(NullPointerException.class, () ->
        new Event("id", "pubkey", 1L, 1, new ArrayList<Tag>(), "content", null));
  }
}