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
  - [NIP-99](https://nostr-nips.com/nip-99) (Classified Listings)
    - [ScdMatrix](https://github.com/avlo/scdecisionmatrix) client implementation (in progress)

  #### In-Progress
  - [NIP-15](https://nostr-nips.com/nip-15) (Nostr Marketplace)
    - [ScdMatrix](https://github.com/avlo/scdecisionmatrix) server implementation
  - [NIP-75](https://nostr-nips.com/nip-75) (Zap Goals / Lightning Network Payments)

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

## Build and install nostr-relay server

    $ cd <your_git_home_dir>
    $ git clone https://github.com/avlo/nostr-relay
    $ cd nostr-relay
    $ mvn clean install

## Run nostr-relay server

    $ cd <your_git_home_dir>/nostr-relay
    $ mvn spring-boot:run
    
or full/debug console logging

    $ cd <your_git_home_dir>/nostr-relay
    $ mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE

## Relay Endpoint for clients

  ws://localhost:5555

## Default/embedded H2 DB console: ##

    localhost:5555/h2-console/

*user: sa*  
*password: // blank* 

Display all framework table contents (case-sensitive quoted fields/tables when querying):

	select id, event_id, kind, nip, content, created_at, pub_key, signature from event;
	select id, "key" as "key", "value" as "value", marker, recommended_relay_url from base_tag;
	select id, base_tag_id, event_id from "event-base_tag-join";
	select id, title, summary, published_at, location from classified_listing;
	select id, classified_listing_id, event_id from "classified_listing-event-join";
	select id, number, currency, frequency from price_tag;
	select id, price_tag_id, event_id from "event-price_tag-join";
	select id, active, subscriber_id, session_id from subscriber;
	select id, subscriber_id, "since", "until", "limit" from "subscriber-filter-join";
	select id, filter_id, event_id from "subscriber-filter_event-join";
	select id, filter_id, author from "subscriber-filter_author-join";
	select id, filter_id, kind_id from "subscriber-filter_kind-join";
	select id, filter_id, referenced_event_id from "subscriber-filter_referenced_event-join";
	select id, filter_id, referenced_pubkey from "subscriber-filter_referenced_pubkey-join";

##### (Optional Use) bundled web-client URLs for convenience/dev-testing/etc

  http://localhost:5555/NIP01.html

  http://localhost:5555/NIP99.html

  http://localhost:5555/REQ.html


<br>
<hr style="border:2px solid grey">

## Creating relay event-handlers

The nostr-relay framework handles Nostr events via _**event services**_ for related _**event types**_.  These event types are structured (and extended) as defined in tcheeric's [nostr-java API](https://github.com/tcheeric/nostr-java).  After implementing the below two considerations, nostr-relay framework will handle the rest.

---

### Step 1 of 2: Create a new event type: _(the Object Oriented way)_
_(note: It is highly recommended to check tcheeric's [nostr-java-event](https://github.com/tcheeric/nostr-java/tree/main/nostr-java-event) module for an already-existing-and-pertinent event type for your needs **before** creating your own.  If you find what you need there, you can skip this section and jump directly to _**[Create a new event-handler](step-1-of-2:-create-a-new-event-handler/service:-_(the-polymorphic-way)_)**_)_

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
---

### Step 2 of 2: Create a new event handler/service: _(the Polymorphic way)_

Define a new class for your service which implements _[EventServiceIF\<T>](src/main/java/com/prosilion/nostrrelay/service/event/EventServiceIF.java)_ interface:

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

After recompiling and redeploying nostr-relay, your service should now be available and active.
