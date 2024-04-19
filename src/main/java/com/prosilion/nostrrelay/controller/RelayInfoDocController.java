//package com.prosilion.nostrrelay.controller;
//
//import lombok.extern.java.Log;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.logging.Level;
//
//@Log
//@RestController
//@RequestMapping("/")
//public class RelayInfoDocController {
//  public static final String DESCRIPTION = "description";
//  private static final String SAMPLE;
//
//  static {
//
//    SAMPLE = "{\n" +
//        "  \"" + DESCRIPTION + "\": \"nicks RelayInfoDocController\",\n" +
//        "  \"name\": \"nicks RelayInfoDocController\",\n" +
//        "  \"pubkey\": \"52b4a076bcbbbdc3a1aefa3735816cf74993b1b8db202b01c883c58be7fad8bd\",\n" +
//        "  \"software\": \"custom\",\n" +
//        "  \"supported_nips\": [\n" +
//        "    1\n" +
//        "  ],\n" +
//        "  \"version\": \"0.0.1\",\n" +
//        "  \"limitation\": {\n" +
//        "    \"payment_required\": false,\n" +
//        "    \"max_message_length\": 65535,\n" +
//        "    \"max_event_tags\": 2000,\n" +
//        "    \"max_subscriptions\": 20,\n" +
//        "    \"auth_required\": false\n" +
//        "  },\n" +
//        "  \"payments_url\": \"https://localhost\",\n" +
//        "  \"fees\": {\n" +
//        "    \"subscription\": [\n" +
//        "      {\n" +
//        "        \"amount\": 2500000,\n" +
//        "        \"unit\": \"msats\",\n" +
//        "        \"period\": 2592000\n" +
//        "      }\n" +
//        "    ]\n" +
//        "  }\n" +
//        "}";
//  }
//
//  @GetMapping(path = "/"
////      , produces = {NostrMediaType.APPLICATION_NOSTR_JSON_VALUE}
////      , consumes = {MediaType.APPLICATION_JSON_VALUE}
//  )
//  public String requestHeaders(@RequestHeader(value = "Accept") String acceptHeader) {
//    log.log(Level.INFO, "RelayInformationDocumentController requestHeaders: {0}", acceptHeader);
//    return SAMPLE;
//  }
//}