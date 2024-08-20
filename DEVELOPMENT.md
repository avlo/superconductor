```java
███████╗██╗   ██╗██████╗ ███████╗██████╗  ██████╗ ██████╗ ███╗   ██╗██████╗ ██╗   ██╗ ██████╗████████╗ ██████╗ ██████╗
██╔════╝██║   ██║██╔══██╗██╔════╝██╔══██╗██╔════╝██╔═══██╗████╗  ██║██╔══██╗██║   ██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗
███████╗██║   ██║██████╔╝█████╗  ██████╔╝██║     ██║   ██║██╔██╗ ██║██║  ██║██║   ██║██║        ██║   ██║   ██║██████╔╝
╚════██║██║   ██║██╔═══╝ ██╔══╝  ██╔══██╗██║     ██║   ██║██║╚██╗██║██║  ██║██║   ██║██║        ██║   ██║   ██║██╔══██╗
███████║╚██████╔╝██║     ███████╗██║  ██║╚██████╗╚██████╔╝██║ ╚████║██████╔╝╚██████╔╝╚██████╗   ██║   ╚██████╔╝██║  ██║
╚══════╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═════╝  ╚═════╝  ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
```

### Development Mode

- [SOLID](https://www.digitalocean.com/community/conceptual-articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design) engineering principles.  Simple.  Clean.  OO.
  - understandability
  - extensibility / modularization [(_HOW-TO: creating relay event-handlers_)](#adding-newcustom-events-to-superconductor)
  - testing
  - customization


- Dependencies:
  - Java 22
  - Spring [Boot](https://spring.io/projects/spring-boot) 3.3.1
  - Spring [WebSocketSession](https://docs.spring.io/spring-session/reference/guides/boot-websocket.html)  3.2.2
  - Event/Message [nostr-java](https://github.com/tcheeric/nostr-java) API library


- Containerized deployment:
  - [Docker](https://hub.docker.com/_/docker) 27.0.3
  - [Docker Compose](https://docs.docker.com/compose/install/) v2.28.1

----

### Requirements

    $ java -version

>     openjdk version "22" 2024-03-19
>     OpenJDK Runtime Environment (build 22+36-2370)
>     OpenJDK 64-Bit Server VM (build 22+36-2370, mixed mode, sharing)

    $ mvn -version
>     Apache Maven 4.0.0-beta-3 (e92f645c2749eb2a4f5a8843cf01e7441e4b559f)
>     Java version: 22, vendor: Oracle Corporation
>     Default locale: en_US, platform encoding: UTF-8
>     OS name: "linux", version: "5.15.0-112-generic", arch: "amd64", family: "unix"

----

### Build Superconductor
#### Build and install nostr-java dependency library
_note: below [java22 nostr-java variant](https://github.com/avlo/nostr-java-avlo-fork/tree/java22) (of tcheeric's [java19 nostr-java library](https://github.com/tcheeric/nostr-java)) supports virtual threads.  reversion to his library will occur upon its upgrade to java22._

    $ cd <your_git_home_dir>
    $ git clone git@github.com:avlo/nostr-java-avlo-fork.git
    $ cd nostr-java-avlo-fork
    $ git checkout java22
    $ mvn clean install

#### Build and install SuperConductor

    $ cd <your_git_home_dir>
    $ git clone https://github.com/avlo/superconductor
    $ cd superconductor
    $ mvn clean install

----

### Run SuperConductor (4 options)

#### 1.  Docker + Docker Compose
##### Confirm minimal docker requirements
    $ docker --version
>     Docker version 27.0.3
    $ docker compose version
>     Docker Compose version v2.28.1

##### Dockerize project
Superconductor spring boot docker uses [buildpacks](https://buildpacks.io/) ([preferential over Dockerfile](https://reflectoring.io/spring-boot-docker/))

    $ mvn -N wrapper:wrapper
    $ mvn spring-boot:build-image

##### Start docker containers
    $ docker compose -f docker-compose-dev.yml up -d

Superconductor is now ready to use.

##### Stop docker containers
    $ docker compose -f docker-compose-dev.yml stop superconductor-app superconductor-db

##### Remove docker containers
    $ docker compose -f docker-compose-dev.yml down --remove-orphans

----

### 2.  Run locally using maven spring-boot:run target

    $ cd <your_git_home_dir>/superconductor
    $ mvn spring-boot:run -Dspring.profiles.active=local 

for full/debug developer console logging:

    $ cd <your_git_home_dir>/superconductor
    $ mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE
----

### 3.  Run locally as executable jar

    $ cd <your_git_home_dir>/superconductor
    $ java -jar target/superconductor-1.7.2.war

----
### 4.  Run using pre-existing local application-server-container instance

    $ cp <your_git_home_dir>/superconductor/target/superconductor-1.7.2.war <your_container/instance/deployment_directory>

----

### Relay Endpoint for clients

ws://localhost:5555

<hr style="border:2px solid grey">

### Default/embedded H2 DB console (local non-docker development mode): ##

    localhost:5555/h2-console/

*user: sa*  
*password: // blank*

Display all framework table contents (case-sensitive quoted fields/tables when querying):

	select id, pub_key, session_id, challenge from auth;
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
	select id, event_id, price_tag_id from "event-price_tag-join";
	select id, uri from relays_tag;
	select id, event_id, relays_id from "event-relays_tag-join";
	select id, number, currency, frequency from price_tag;

##### (Optional Use) bundled web-client URLs for convenience/dev-testing/etc

http://localhost:5555/api-tests.html <sup>_(nostr **events** web-client)_</sup>

http://localhost:5555/request-test.html <sup>_(nostr **request** web-client)_</sup>
<br>
<hr style="border:2px solid grey">

### Adding new/custom events to SuperConductor

For Nostr clients generating canonical Nostr JSON (as defined in [NIP01 spec: Basic protocol flow description, Events, Signatures and Tags](https://nostr-nips.com/nip-01)), SuperConductor will automatically recognize those JSON events- including their database storage, retrieval and subscriber notification.  No additional work or customization is necessary.
<br>
<hr style="border:2px solid grey">

### Adding new/custom tags to SuperConductor

SuperConductor supports any _**generic**_ tags automatically.  Otherwise, if custom tag structure is required, simply implement the [`TagPlugin`](https://github.com/avlo/superconductor/blob/master/src/main/java/com/prosilion/superconductor/dto/TagPlugin.java) interface (an example can be seen [here](https://github.com/avlo/superconductor/blob/master/src/main/java/com/prosilion/superconductor/dto/EventTagPlugin.java)) and your tag will automatically get included by SuperConductor after rebuilding and redeploying.

