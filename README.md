```java
███████╗██╗   ██╗██████╗ ███████╗██████╗  ██████╗ ██████╗ ███╗   ██╗██████╗ ██╗   ██╗ ██████╗████████╗ ██████╗ ██████╗
██╔════╝██║   ██║██╔══██╗██╔════╝██╔══██╗██╔════╝██╔═══██╗████╗  ██║██╔══██╗██║   ██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗
███████╗██║   ██║██████╔╝█████╗  ██████╔╝██║     ██║   ██║██╔██╗ ██║██║  ██║██║   ██║██║        ██║   ██║   ██║██████╔╝
╚════██║██║   ██║██╔═══╝ ██╔══╝  ██╔══██╗██║     ██║   ██║██║╚██╗██║██║  ██║██║   ██║██║        ██║   ██║   ██║██╔══██╗
███████║╚██████╔╝██║     ███████╗██║  ██║╚██████╗╚██████╔╝██║ ╚████║██████╔╝╚██████╔╝╚██████╗   ██║   ╚██████╔╝██║  ██║
╚══════╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═════╝  ╚═════╝  ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
```
# Java Nostr-Relay Framework & Web Application
- Simple.  Clean.  OO.
  - Java 20
  - Spring [WebSocketSession 3.2.2](https://docs.spring.io/spring-session/reference/guides/boot-websocket.html)
  - Spring Boot 3.2.4
  - Event/Message [nostr-java](https://github.com/tcheeric/nostr-java) library by tcheeric
    
- core [SOLID OO](https://www.digitalocean.com/community/conceptual-articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design) principles, providing ease of:
  - understandability
  - extensibility / modularization [(_HOW-TO: creating relay event-handlers_)](#creating-relay-event-handlers)
  - customization
  - testing

## NIPS
  #### Supported
  - [NIP-01](https://nostr-nips.com/nip-01) (Basic protocol & Standard Tags)
  - [NIP-10](https://nostr-nips.com/nip-10) (Marked "e" tags)
  - [NIP-11](https://nostr-nips.com/nip-75) (Relay Information Document)
  - [NIP-11](https://nostr-nips.com/nip-12) (Generic Tag Queries)
  - [NIP-14](https://nostr-nips.com/nip-14) (Subject tag in Text events)
  - [NIP-16](https://nostr-nips.com/nip-16) (Event treatment)
  - [NIP-19](https://nostr-nips.com/nip-19) (Bech-32 encoded entities)
  - [NIP-19](https://nostr-nips.com/nip-20) (Command Results)
  - [NIP-21](https://nostr-nips.com/nip-21) (URI scheme)
  - [NIP-31](https://nostr-nips.com/nip-31) (Unknown event kinds)
  - [NIP-22](https://nostr-nips.com/nip-22) (Event "created_at" limits)
  - [NIP-57](https://nostr-nips.com/nip-57) (Lightning Zaps)
  - [NIP-99](https://nostr-nips.com/nip-99) (Classified Listings)
    - used by [Barchetta](https://github.com/avlo/barchetta) Smart-Contract Negotiation Protocol (in progress) atop [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin) [Lightning-Network](https://en.wikipedia.org/wiki/Lightning_Network) [RGB](https://rgb.tech/)

  #### In-Progress
  - [NIP-15](https://nostr-nips.com/nip-15) (Nostr Marketplace)
    - used by [Barchetta](https://github.com/avlo/barchetta) Smart-Contract Negotiation Protocol (in progress) atop [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin) [Lightning-Network](https://en.wikipedia.org/wiki/Lightning_Network) [RGB](https://rgb.tech/)

## Requirements

    $ java -version

>     openjdk version "20.0.1" 2023-04-18
>     OpenJDK Runtime Environment (build 20.0.1+9-29)
>     OpenJDK 64-Bit Server VM (build 20.0.1+9-29, mixed mode, sharing)

    $ mvn -version
>     Apache Maven 3.9.2 (c9616018c7a021c1c39be70fb2843d6f5f9b8a1c)
>     Maven home: ~/.sdkman/candidates/maven/current
>     Java version: 20.0.1, vendor: Oracle Corporation, runtime: ~/.sdkman/candidates/java/20.0.1-open
>     Default locale: en_US, platform encoding: UTF-8
>     OS name: "linux", version: "5.15.0-72-generic", arch: "amd64", family: "unix"

## Build and install nostr-java library

    $ cd <your_git_home_dir>
    $ git clone git@github.com:tcheeric/nostr-java.git
    $ cd nostr-java
    $ git checkout develop
    $ mvn clean install

## Build and install SuperConductor

    $ cd <your_git_home_dir>
    $ git clone https://github.com/avlo/superconductor
    $ cd superconductor
    $ mvn clean install

## Run SuperConductor

    $ cd <your_git_home_dir>/superconductor
    $ mvn spring-boot:run
    
or full/debug console logging

    $ cd <your_git_home_dir>/superconductor
    $ mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE

## Relay Endpoint for clients

  ws://localhost:5555

## Default/embedded H2 DB console: ##

    localhost:5555/h2-console/

*user: sa*  
*password: // blank* 

Display all framework table contents (case-sensitive quoted fields/tables when querying):

	select id, event_id_string, kind, nip, created_at, pub_key, content from event;
	select id, event_id, event_tag_id from "event-event_tag-join";
	select id, event_id_string, recommended_relay_url, marker from event_tag;
	select id, event_id, pubkey_id from "event-pubkey_tag-join";
	select id, public_key, main_relay_url, pet_name from pubkey_tag;
	select id, event_id, subject_tag_id from "event-subject_tag-join";
	select id, subject from subject_tag;
	select id, hashtag_tag from hashtag_tag;
	select id, location from geohash_tag;
	select id, event_id, geohash_tag_id from "event-geohash_tag-join";
	select id, event_id, hash_tag_id from "event-hashtag_tag-join";
	select id, event_id, generic_tag_id  FROM "event-generic_tag-join";
	select id, code from generic_tag;
	select id, generic_tag_id, element_attribute_id from "generic_tag-element_attribute-join";
	select id, name, "value" from element_attribute;
	select id, subscriber_id, session_id, active from subscriber;
	select id, subscriber_id, "since", "until", "limit" from "subscriber-filter-join";
	select id, referenced_event_id, filter_id from "subscriber-filter_referenced_event-join";
	select id, referenced_pubkey, filter_id from "subscriber-filter_referenced_pubkey-join";
	select id, filter_id, kind_id from "subscriber-filter_kind-join";
	select id, filter_id, event_id from "subscriber-filter_event-join";
	select id, filter_id, author from "subscriber-filter_author-join";
	select id, recipient_pub_key, amount, ln_url from zaprequest;
	select id, event_id, price_tag_id from "event-price_tag-join";
	select id, uri from relays_tag;
	select id, number, currency, frequency from price_tag;
 
##### (Optional Use) bundled web-client URLs for convenience/dev-testing/etc

  http://localhost:5555/NIP01.html

  http://localhost:5555/NIP99.html

  http://localhost:5555/REQ.html
<br>
<hr style="border:2px solid grey">

# Adding new/custom events to SuperConductor

For Nostr clients generating canonical Nostr JSON (as defined in [NIP01 spec: Basic protocol flow description, Events, Signatures and Tags](https://nostr-nips.com/nip-01)), SuperConductor will automatically recognize those JSON events- including their database storage, retrieval and subscriber notification.  No additional work or customization is necessary.
<br>
<hr style="border:2px solid grey">

# Adding new/custom tags to SuperConductor

SuperConductor supports any _**generic**_ tags automatically.  Otherwise, if custom tag structure is required, simply implement the [`TagPlugin`](https://github.com/avlo/superconductor/blob/master/src/main/java/com/prosilion/superconductor/dto/TagPlugin.java) interface (an example can be seen [here](https://github.com/avlo/superconductor/blob/master/src/main/java/com/prosilion/superconductor/dto/EventTagPlugin.java)) and your tag will automatically get included by SuperConductor after rebuilding and redeploying.

