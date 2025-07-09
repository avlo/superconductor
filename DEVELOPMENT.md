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


### Dependencies:
- Java 21 (or higher)
- [nostr-java-core](https://github.com/avlo/nostr-java-core) (nostr events & tags, event messages, request messages & filters)
  
  #### Internal implementation (auto-imported):
  - Spring [Boot](https://spring.io/projects/spring-boot) 3.4.3
  - Spring [WebSocketSession](https://docs.spring.io/spring-session/reference/guides/boot-websocket.html)
  - Spring [WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) Reactive Web-Socket Client

### Containerized deployment:
- [Docker](https://hub.docker.com/_/docker) 27.5.0
- [Docker Compose](https://docs.docker.com/compose/install/) v2.32.4
----

### Requirements

    $ java -version

>     java version "21.0.5" 2024-10-15 LTS
>     Java(TM) SE Runtime Environment (build 21.0.5+9-LTS-239)
>     Java HotSpot(TM) 64-Bit Server VM (build 21.0.5+9-LTS-239, mixed mode, sharing)

<details>
  <summary>maven</summary>

    $ mvn -version
>     Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
>     Java version: 21.0.5, vendor: Oracle Corporation
</details>
<details>
  <summary>gradle</summary>

    $ gradle -version
>     ------------------------------------------------------------
>     Gradle 8.13
>     ------------------------------------------------------------
</details>

----

### Build Superconductor
#### 1. Check-out nostr-java-core dependency library

    $ cd <your_git_home_dir>
    $ git clone git@github.com:avlo/nostr-java-core.git
    $ cd nostr-java-core
    $ git checkout develop

#### 2. Check-out SuperConductor

    $ cd <your_git_home_dir>
    $ git clone https://github.com/avlo/superconductor
    $ cd superconductor
    $ git checkout develop

#### 3. Configure JUnit / SpringBootTest security mode via [appication-test.properties](src/test/resources/application-test.properties) file
<details>
  <summary>Default: Non-Secure (WS) tests mode</summary>

    # ws autoconfigure
    # security test (ws) disabled ('false') by default.
    server.ssl.enabled=false                                           <--------  "false" for ws/non-secure
    # ...
    superconductor.relay.url=ws://localhost:5555                       <--------  "ws" protocol for ws/non-secure
</details>
<details>
  <summary>Custom: Secure (WSS/TLS) tests mode</summary>

    # wss autoconfigure
    # to enable secure tests (wss), change below value to 'true' and...
    server.ssl.enabled=true                                            <--------  "true" for wss/secure
    # ...also for secure (wss), change below value to 'wss'...
    superconductor.relay.url=wss://localhost:5555                      <--------  "wss" protocol for wss/secure

   Configure SuperConductor run-time security, 3 options:

  | SecurityLevel | Specification                                                        | Details                                                                                                                                                                                                                                                                                                                                                                                 |
  |---------------|----------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | Highest       | SSL Certificate WSS/HTTPS<br>(industry standard secure encrypted)    | 1. [Obtain](https://www.websitebuilderexpert.com/building-websites/how-to-get-an-ssl-certificate/) an SSL certificate.<br>2. [Install](https://www.baeldung.com/java-import-cer-certificate-into-keystore) the certificate<br>3. Enable [SSL configuration options](src/main/resources/application-local_wss.properties?plain=1#L6,8,L11-L15) in application-local_wss/dev_wss.properties file. |
  | Medium        | Self-Signed Certificate WSS/HTTPS (locally created secure encrypted) | 1. Create a [Self-Signed Certificate](https://www.baeldung.com/openssl-self-signed-cert).<br>2. [Install](https://www.baeldung.com/java-import-cer-certificate-into-keystore) the certificate<br>3. Enable [SSL configuration options](src/main/resources/application-local_wss.properties?plain=1#L6,8,L11-L15) in application-local_wss/dev_wss.properties file.                      |
  | None/Default  | WS/HTTP<br>non-secure / non-encrypted                                | Security-related configuration(s) not required                                                                                                                                                                                                                                                                                                                                          |  

</details>

#### 4. Configure project properties
supply required values in [autotest.properties](autotest.properties) file:
```xml
M2_NOSTR_JAVA_REPO=<your_local_m2_nostr_java_repo>
NOSTR_JAVA_HOME=<your_local_nostr_java_home>
SUPERCONDUCTOR_HOME=<your_local_superconductor_home>  
```
for example:
```bash
M2_NOSTR_JAVA_REPO=/home/<username>/.m2/repository/com/prosilion
NOSTR_JAVA_HOME=/home/<username>/git/nostr-java-core
SUPERCONDUCTOR_HOME=/home/<username>/git/superconductor
```
#### 5.  Build application (both unit-test and integration-test included)
```bash
$ cd <your_git_home_dir>
$ cd superconductor
$ . ./autotest.sh
```
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
    $ mvn spring-boot:build-image -pl superconductor -Dmaven.test.skip=true

(*optionally edit [superconductor/docker-compose-dev_wss.yml](superconductor/docker-compose-dev_wss.yml?plain=1#L10,L32,L36-L37) parameters as applicable.*)

##### Start docker containers

<details>
  <summary>WSS/HTTPS</summary>  

run without logging:

    docker compose -f superconductor/docker-compose-dev_wss.yml up 

run with container logging displayed to console:

    docker compose -f superconductor/docker-compose-dev_wss.yml up --abort-on-container-failure --attach-dependencies

run with docker logging displayed to console:

    docker compose -f superconductor/docker-compose-dev_wss.yml up -d && dcls | grep 'superconductor-app' | awk '{print $1}' | xargs docker logs -f
</details> 

<details>
  <summary>WS/HTTP</summary>  

run without logging:

    docker compose -f superconductor/docker-compose-dev_ws.yml up 

run with container logging displayed to console:

    docker compose -f superconductor/docker-compose-dev_ws.yml up --abort-on-container-failure --attach-dependencies

run with docker logging displayed to console:

    docker compose -f superconductor/docker-compose-dev_ws.yml up -d && dcls | grep 'superconductor-app' | awk '{print $1}' | xargs docker logs -f
</details> 

----

##### Stop docker containers

<details>
  <summary>WSS/HTTPS</summary>

    docker compose -f superconductor/docker-compose-dev_wss.yml stop superconductor superconductor-db
</details> 

<details>
  <summary>WS/HTTP</summary>  

    docker compose -f superconductor/docker-compose-prod_ws.yml stop superconductor superconductor-db
</details>

----  

##### Remove docker containers

<details>
  <summary>WSS/HTTPS</summary>

    docker compose -f superconductor/docker-compose-dev_wss.yml down --remove-orphans
</details> 

<details>
  <summary>WS/HTTP</summary>  

    docker compose -f superconductor/docker-compose-prod_ws.yml down --remove-orphans
</details>  

----

### 2.  Run locally using spring-boot:run target
      $ cd <your_git_home_dir>/superconductor

<details>
  <summary>WSS/HTTPS</summary>

###### maven
      $ mvn spring-boot:run -pl superconductor -P local_wss
###### gradle
      $ gradle superconductor-webapp:bootRunLocalWss
</details> 

<details>
  <summary>WS/HTTP</summary>

###### maven
      $ mvn spring-boot:run -pl superconductor -P local_ws
###### gradle
      $ gradle superconductor-webapp:bootRunLocalWs
</details>  

----

### 3.  Run locally as executable jar

    $ cd <your_git_home_dir>/superconductor
    $ java -jar superconductor/target/superconductor-1.15.1.war  

----

### 4.  Run using pre-existing local application-server-container instance

    $ cp <your_git_home_dir>/superconductor/superconductor/target/superconductor-1.15.1.war <your_container/instance/deployment_directory>

----

### Relay Endpoint for clients

<details>
  <summary>WSS/HTTPS</summary>

    wss://localhost:5555
</details> 

<details>
  <summary>WS/HTTP</summary>  

    ws://localhost:5555
</details>

<hr style="border:2px solid grey">

### Default/embedded H2 DB console (local non-docker development mode): ##

    localhost:5555/h2-console/

*user: sa*  
*password: // blank*

Display all framework table contents (case-sensitive quoted fields/tables when querying):

	-- select id, pub_key, session_id, challenge from auth;
	select id, event_id_string, kind, created_at, pub_key, content, concat(left(signature,20), '...') as signature from event;
	select id, event_id, event_tag_id from "event-event_tag-join";
	select id, event_id_string, recommended_relay_url, marker from event_tag;
	select id, event_id, pubkey_id from "event-pubkey_tag-join";
	select id, public_key, main_relay_url, pet_name from pubkey_tag;
	select id, event_id, identifier_tag_id from "event-identifier_tag-join";
	select id, uuid from identifier_tag;
	select id, event_id, address_tag_id from "event-address_tag-join";
	select id, kind, pub_key, uuid, relay_uri, code from address_tag;
	select id, event_id, reference_tag_id from "event-reference_tag-join";
	select id, uri from reference_tag;
	select id, event_id, subject_tag_id from "event-subject_tag-join";
	select id, subject from subject_tag;
	select id, event_id, hash_tag_id from "event-hashtag_tag-join";
	select id, hashtag_tag from hashtag_tag;
	select id, event_id, geohash_tag_id from "event-geohash_tag-join";
	select id, location from geohash_tag;
	select id, event_id, generic_tag_id  FROM "event-generic_tag-join";
	select id, code from generic_tag;
	select id, generic_tag_id, element_attribute_id from "generic_tag-element_attribute-join";
	select id, name, "value" from element_attribute;
	select id, event_id, price_tag_id from "event-price_tag-join";
	select id, number, currency, frequency from price_tag;
	select id, event_id from deletion_event;

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

SuperConductor supports any _**generic**_ tags automatically.  Otherwise, if custom tag structure is required, simply implement the [`TagPlugin`](lib/src/main/java/com/prosilion/superconductor/plugin/tag/TagPlugin.java) interface (see [EventTagPlugin](lib/src/main/java/com/prosilion/superconductor/plugin/tag/EventTagPlugin.java) example) and your tag will automatically get included by SuperConductor after rebuilding and redeploying.

