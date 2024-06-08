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
  - [NIP-01](https://nostr-nips.com/nip-01) (Basic protocol)
  - [NIP-11](https://nostr-nips.com/nip-75) (Relay Information Document)
  - [NIP-14](https://nostr-nips.com/nip-14) (Subjects)
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

	select id, event_id, kind, nip, created_at, pub_key, signature, content from event;
	select id, id_event, recommended_relay_url, marker from event_tag;
	select id, public_key, main_relay_url, pet_name from pubkey_tag;
	select id, event_id, event_tag_id from "event-event_tag-join";
	select id, event_id, pubkey_id from "event-pubkey_tag-join";
	select id, event_id, subject_tag_id from "event-subject_tag-join";
	select id, subject from subject_tag;
	select id, code, hash_tag from hashtag_tag;
	select id, code, location from geohash_tag;
	select id, event_id, geohash_tag_id from "event-geohash_tag-join";
	select id, event_id, hash_tag_id from "event-hashtag_tag-join";
	select id, subscriber_id, session_id, active from subscriber;
	select id, subscriber_id, "since", "until", "limit" from "subscriber-filter-join";
	select id, referenced_event_id, filter_id from "subscriber-filter_referenced_event-join";
	select id, referenced_pubkey, filter_id from "subscriber-filter_referenced_pubkey-join";
	select id, filter_id, kind_id from "subscriber-filter_kind-join";
	select id, filter_id, event_id from "subscriber-filter_event-join";
	select id, filter_id, author from "subscriber-filter_author-join";
	select id, recipient_pub_key, amount, ln_url from zaprequest;
	select id, event_id, classified_listing_id from "classified_listing-event-join";
	select id, event_id, price_tag_id from "event-price_tag-join";
	select id, event_id, zap_request_event_id from "zaprequest_event-event-join";
	select id, title, published_at, location, summary from classified_listing;
	select id, uri from relays_tag;
	select id, number, currency, frequency from price_tag;
 
##### (Optional Use) bundled web-client URLs for convenience/dev-testing/etc

  http://localhost:5555/NIP01.html

  http://localhost:5555/NIP99.html

  http://localhost:5555/REQ.html


<br>
<hr style="border:2px solid grey">

# Creating relay event-handlers

The SuperConductor framework handles Nostr events via _**event services**_ for related _**event types**_.  These event types are structured (and extended) as defined in tcheeric's [nostr-java API](https://github.com/tcheeric/nostr-java).  After implementing the below two considerations, SuperConductor will handle the rest.

## Step 1 of 2: Create a new event type: _(the Object Oriented way)_
_(note: It is highly recommended to check tcheeric's [nostr-java-event](https://github.com/tcheeric/nostr-java/tree/main/nostr-java-event) module for an already-existing-and-pertinent event type for your needs **before** creating your own event type.  If you find what you need there, you can skip this section and jump directly to _**[Create a new event-handler](step-1-of-2:-create-a-new-event-handler/service:-_(the-polymorphic-way)_)**_)_

Define a new class for your event, minimally as follows:

```java
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

public class YourNewEvent extends GenericEvent {                                  <--- extend GenericEvent
  public YourNewEvent(PublicKey pubKey, List<BaseTag> tags, String content) {     <--- provide minimal args constructor
    super(pubKey, Kind.YOUR_NEW_EVENT_KIND, tags, content);                       <--- call parent constructor passing YOUR_NEW_EVENT_KIND
  }
}
```
## Step 2 of 2: Create a new event handler/service: _(the Polymorphic way)_

Define a new class for your service which implements _[EventServiceIF\<T>](src/main/java/com/prosilion/superconductor/service/event/EventServiceIF.java)_ interface:

```java
public interface EventServiceIF<T extends EventMessage> {
  void processIncoming(T eventMessage);
  Kind getKind();
}
```

for example (_leveraging [decorator pattern](https://www.digitalocean.com/community/tutorials/decorator-design-pattern-in-java-example)_):

```java
import org.springframework.stereotype.Service;

@Service                                                                                 <--- SpringWebMVC managed bean
public class YourNewEventService<T extends EventMessage> implements EventServiceIF<T> {  <--- implement EventServiceIF<T> interface
  public final Kind kind = Kind.YOUR_NEW_EVENT_KIND;                                     <--- define YOUR_NEW_EVENT_KIND
  private final EventService<YourNewEvent> eventService;

  @Autowired
  public YourNewEventService(EventService<YourNewEvent> eventService) {       <--- constructor EventService<YourNewEvent> bean parameter
    this.eventService = eventService;                                              provides decorator behavior
  }

  @Override
  public void processIncoming(T eventMessage) {                               <--- implement EventServiceIF<T> interface method
    GenericEvent event = (GenericEvent) eventMessage.getEvent();              <--- example business logic ┐
    event.setNip(YOUR_NIP_VALUE);                                                                         |
    event.setKind(Kind.YOUR_NEW_EVENT_KIND.getValue());                                                   |
    YourNewEvent yourNewEvent = new YourNewEvent(                                                         |
        event.getPubKey(),                                                                                |
        event.getTags(),                                                                                  |
        event.getContent()                                                   <----------------------------┘
    );
    Long id = eventService.saveEventEntity(event);                           <--- save to DB
    yourNewEvent.setId(event.getId());                                       <--- update event id
    eventService.publishEvent(id, yourNewEvent);                             <--- publish event to service
  }
}
```
After recompiling and redeploying SuperConductor, your service should now be available and active.
<br>
<hr style="border:2px solid grey">

# Hooking event types into to SuperConductor DB

For most/canonical Nostr events (as per [NIP01 spec: Basic protocol flow description, Events, Signatures and Tags](https://nostr-nips.com/nip-01)), SuperConductor will likely already handle all necessary event storage and retrieval considerations.  Otherwise, the following is a HOW-TO for new/exceptional cases:

Under Construction

<img src="https://media.tenor.com/MRCIli40TYoAAAAi/under-construction90s-90s.gif" width="30"/>
