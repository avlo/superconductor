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
  - Java 21
  - Spring [Boot](https://spring.io/projects/spring-boot) 3.3.4
  - Spring [WebSocketSession](https://docs.spring.io/spring-session/reference/guides/boot-websocket.html)  3.3.4
  - Event/Message [nostr-java](https://github.com/avlo/nostr-java-avlo-fork/tree/develop)   (symmetric fork of tcheeric's [nostr-java](https://github.com/tcheeric/nostr-java/tree/develop)) API/library


- Containerized deployment:
  - [Docker](https://hub.docker.com/_/docker) 27.5.0
  - [Docker Compose](https://docs.docker.com/compose/install/) v2.32.4

----

### Requirements

    $ java -version

>     java version "21.0.5" 2024-10-15 LTS
>     Java(TM) SE Runtime Environment (build 21.0.5+9-LTS-239)
>     Java HotSpot(TM) 64-Bit Server VM (build 21.0.5+9-LTS-239, mixed mode, sharing)

    $ mvn -version
>     Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
>     Java version: 21.0.5, vendor: Oracle Corporation

----

### Build Superconductor
#### Build and install nostr-java dependency library

    $ cd <your_git_home_dir>
    $ git clone git@github.com:avlo/nostr-java-avlo-fork.git
    $ cd nostr-java-avlo-fork
    $ git checkout develop
    $ mvn clean install

#### Build and install SuperConductor

    $ cd <your_git_home_dir>
    $ git clone https://github.com/avlo/superconductor
    $ cd superconductor
    $ mvn clean install

----

### JUnit / SpringBootTest Superconductor
##### Two test modes, configurable via [appication-test.properties](src/test/resources/application-test.properties) file
#### 1. Non-Secure (WS) tests mode (default)
```
# ws autoconfigure
# security test (ws) disabled ('false') by default.
server.ssl.enabled=false                                           <--------  "false" for ws/non-secure
# ...
superconductor.relay.url=ws://localhost:5555                       <--------  "ws" protocol for ws/non-secure 
```

##### 2. Secure (WSS/TLS) tests mode
```
# wss autoconfigure
# to enable secure tests (wss), change below value to 'true' and...
server.ssl.enabled=true                                            <--------  "true" for wss/secure
# ...also for secure (wss), change below value to 'wss'...
superconductor.relay.url=wss://localhost:5555                      <--------  "wss" protocol for wss/secure 
```

----

#### Configure SuperConductor run-time security, 3 options:

| SecurityLevel | Specification                                                        | Details                                                                                                                                                                                                                                                                                                                                                                             |
|---------------|----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Highest       | SSL Certificate WSS/HTTPS<br>(industry standard secure encrypted)    | 1. [Obtain](https://www.websitebuilderexpert.com/building-websites/how-to-get-an-ssl-certificate/) an SSL certificate.<br>2. [Install](https://www.baeldung.com/java-import-cer-certificate-into-keystore) the certificate<br>3. Enable [SSL configuration options](src/main/resources/application-local.properties?plain=1#L6,8,L11-L15) in application-local/dev.properties file. |
| Medium        | Self-Signed Certificate WSS/HTTPS (locally created secure encrypted) | 1. Create a [Self-Signed Certificate](https://www.baeldung.com/openssl-self-signed-cert).<br>2. [Install](https://www.baeldung.com/java-import-cer-certificate-into-keystore) the certificate<br>3. Enable [SSL configuration options](src/main/resources/application-local.properties?plain=1#L6,8,L11-L15) in application-local/dev.properties file.                              |
| None/Default  | WS/HTTP<br>non-secure / non-encrypted                                | For expedient/immediate use without need for an [SSL/TLS certificate](https://www.cloudflare.com/learning/ssl/what-is-an-ssl-certificate/), SuperConductor WebSockets default to WS/HTTP (non-secure/non-encrypted) mode.                                                                                                                                                           |  

----

### Run SuperConductor (4 options)

#### 1.  Docker + Docker Compose
##### Confirm minimal docker requirements
    $ docker --version
>     Docker version 27.5.0
    $ docker compose version
>     Docker Compose version v2.32.4

_(note: Confirmed compatible with Docker 27.0.3 and Docker Compose version v2.28.1 or higher.  Earlier versions are at the liability of the developer/administrator)_
##### Dockerize project
Superconductor spring boot docker uses [buildpacks](https://buildpacks.io/) ([preferential over Dockerfile](https://reflectoring.io/spring-boot-docker/))

    $ mvn -N wrapper:wrapper
    $ mvn spring-boot:build-image

(*optionally edit various [docker-compose-dev.yml](docker-compose-dev.yml?plain=1#L10,L32,L36-L37) parameters as applicable.*)

##### Start docker containers
    $ docker compose -f docker-compose-dev.yml up -d

Superconductor is now ready to use.

##### Stop docker containers
    $ docker compose -f docker-compose-dev.yml stop superconductor superconductor-db

##### Remove docker containers
    $ docker compose -f docker-compose-dev.yml down --remove-orphans

----

### 2.  Run locally using maven spring-boot:run target

    $ cd <your_git_home_dir>/superconductor
    $ mvn spring-boot:run -P local 

for full/debug developer console logging:

    $ cd <your_git_home_dir>/superconductor
    $ mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE -P local
----

### 3.  Run locally as executable jar

    $ cd <your_git_home_dir>/superconductor
    $ java -jar target/superconductor-1.10.0.war

----
### 4.  Run using pre-existing local application-server-container instance

    $ cp <your_git_home_dir>/superconductor/target/superconductor-1.10.0.war <your_container/instance/deployment_directory>

----

### Relay Endpoint for clients
#### Two modes
##### 1. Secure (WSS/TLS) mode (default)
wss://localhost:5555

#### 2. Non-Secure (WS) mode, configurable via _application-**\<env>**.properties_ file (for example, in [appication-local.properties](src/main/resources/application-local.properties))
```
# wss autoconfigure
# secure (wss) enabled 'true' by default.  for non-secure (ws), change below value to 'false' and...
server.ssl.enabled=false                                           <--------  "false" for ws/non-secure
# ...also for non-secure (ws), change below value to 'ws'...
superconductor.relay.url=ws://localhost:5555                       <--------  "ws" protocol for ws/non-secure 
```
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
    select id, identifier from identifier_tag;
    select id, event_id, geohash_tag_id from "event-geohash_tag-join";
    select id, event_id, hash_tag_id from "event-hashtag_tag-join";
    select id, event_id, generic_tag_id from "event-generic_tag-join";
    select id, event_id, identifier_tag_id from "event-identifier_tag-join";
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

