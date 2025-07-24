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
  
  #### Implementation dependencies (auto-imported):
  - [nostr-java-core](https://github.com/avlo/nostr-java-core) (nostr events & tags, event messages, request messages & filters)
  - Spring [Boot](https://spring.io/projects/spring-boot) 3.5.1
  - Spring [WebSocketSession](https://docs.spring.io/spring-session/reference/guides/boot-websocket.html)
  - Spring [WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) Reactive Web-Socket Client
  #### Test dependencies (auto-imported):
  - [Subdivisions](https://github.com/avlo/subdivisions) (Reactive java web-socket client)
  - [Spring Boot Test](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)
  - [Spring JUnit Jupiter](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-junit-jupiter.html)

### (Optional, recommended) Containerized deployment:
- [Docker](https://hub.docker.com/_/docker) 27.5.0
- [Docker Compose](https://docs.docker.com/compose/install/) v2.32.4

----

### Development Requirements

    $ java -version

>     java version "21.0.5" 2024-10-15 LTS
>     Java(TM) SE Runtime Environment (build 21.0.5+9-LTS-239)
>     Java HotSpot(TM) 64-Bit Server VM (build 21.0.5+9-LTS-239, mixed mode, sharing)

<details><summary>maven</summary>
    <blockquote>

###### (unix)

```bash
$ ./mvnw -version

Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Java version: 21.0.5, vendor: Oracle Corporation
```

###### (windows)

```bash
$ ./mvnw.cmd -version

Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Java version: 21.0.5, vendor: Oracle Corporation
``` 

</blockquote>
</details>

<details><summary>gradle</summary>
    <blockquote>

###### (unix)

```bash
$ ./gradlew -version
------------------------------------------------------------
Gradle 8.13
------------------------------------------------------------
```

###### (windows)

```bash
$ ./gradlew.bat -version
------------------------------------------------------------
Gradle 8.13
------------------------------------------------------------
```

</blockquote>
</details>

_likely to work with maven 3.8.x and/or gradle 8.x_, with above specified versions (or higher) guaranteed to work 

----

### [Spring-Boot Starter](https://docs.spring.io/spring-boot/tutorial/first-application/index.html) development: for rapid nostr-relay customization & extensibility

#### Repositories configuration

<details><summary>maven</summary>

###### ~/.m2/settings.xml (typically)

```xml
<repositories>
  <repository>
    <id>github-superconductor</id>
    <url>https://maven.pkg.github.com/avlo/superconductor</url>
  </repository>
</repositories>
...
...
</servers>
  <server>
    <id>github-superconductor</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_GITHUB_ACCESS_TOKEN/PASSWORD</password>
  </server>
</servers>
```
</details>

<details><summary>gradle</summary>

###### build.gradle (typically)

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/avlo/superconductor")
    }
}
```
</details>

#### Spring-boot-starter dependency variants for your own Spring-Boot Starter project...

<details><summary>superconductor-spring-boot-starter-h2db</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>

###### pom.xml
```xml
<dependency>
  <groupId>com.prosilion.superconductor</groupId>
  <artifactId>superconductor-spring-boot-starter-h2db</artifactId>
  <version>1.16.0</version>
</dependency>
```
</blockquote>            
        </details>
    </blockquote>
    <blockquote>
        <details><summary>gradle</summary>
            <blockquote>

###### build.gradle
```groovy
implementation 'com.prosilion.superconductor:superconductor-spring-boot-starter-h2db:' + 1.16.0
```
</blockquote>            
        </details>
    </blockquote>
</details>
<details><summary>superconductor-spring-boot-starter-mysql</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>

###### pom.xml
```xml
<dependency>
  <groupId>com.prosilion.superconductor</groupId>
  <artifactId>superconductor-spring-boot-starter-mysql</artifactId>
  <version>1.16.0</version>
</dependency>
```
</blockquote>            
        </details>
    </blockquote>
    <blockquote>
        <details><summary>gradle</summary>
            <blockquote>

###### build.gradle
```groovy
implementation 'com.prosilion.superconductor:superconductor-spring-boot-starter-mysql:' + 1.16.0
```
</blockquote>            
        </details>
    </blockquote>
</details>
<details><summary>superconductor-spring-boot-starter-redis</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>

###### pom.xml
```xml
<dependency>
  <groupId>com.prosilion.superconductor</groupId>
  <artifactId>superconductor-spring-boot-starter-redis</artifactId>
  <version>1.16.0</version>
</dependency>
```
</blockquote>            
        </details>
    </blockquote>
    <blockquote>
        <details><summary>gradle</summary>
            <blockquote>

###### build.gradle
```groovy
implementation 'com.prosilion.superconductor:superconductor-spring-boot-starter-redis:' + 1.16.0
```
</blockquote>            
        </details>
    </blockquote>
</details>

###### included reference implementations:
- [superconductor/h2db](superconductor/h2db)
- [superconductor/mysql](superconductor/mysql)
- [superconductor/redis](superconductor/redis)
- (external / standalone) [Afterimage](https://github.com/avlo/afterimage) Nostr Reputation-Authority Relay (aka, reputation mesh network)

#### ... or core auto-configuration for lower-level extension / customization

<details><summary>superconductor-base</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>

###### pom.xml
```xml
<dependency>
  <groupId>com.prosilion.superconductor</groupId>
  <artifactId>superconductor-base</artifactId>
  <version>1.16.0</version>
</dependency>
```
</blockquote>            
        </details>
    </blockquote>
    <blockquote>
        <details><summary>gradle</summary>
            <blockquote>

###### build.gradle
```groovy
implementation 'com.prosilion.superconductor:superconductor-base:' + 1.16.0
```
</blockquote>            
        </details>
    </blockquote>
</details>

----

### Standard Development
#### Build Superconductor 
##### 1. Check-out superconductor

    $ cd <your_git_home_dir>
    $ git clone https://github.com/avlo/superconductor
    $ cd superconductor
    $ git checkout develop

#### 2.  Build superconductor
<details>
  <summary>maven</summary>  

      $ cd <your_git_home_dir>
      $ cd superconductor

    (unix)
      $ ./mvnw clean compile

    (windows)
      $ ./mvnw.cmd clean compile
</details>

<details>
  <summary>gradle</summary>

      $ cd <your_git_home_dir>
      $ cd superconductor

    (unix)
      $ ./gradlew clean build

    (windows)
      $ ./gradlew.bat clean build
</details>

#### 3. (Optional) Configure JUnit / SpringBootTest security mode via [appication-test.properties](src/test/resources/application-test.properties) file
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

#### 4. (Optional, recommended) Run unit and integration tests

<details><summary>h2db</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>
                <details><summary>unix</summary>
                    <blockquote>
<blockquote>

```bash
$ ./mvnw verify -f superconductor/h2db/pom.xml
```
</blockquote>
                    </blockquote>
                </details>
                <details><summary>windows</summary>
                    <blockquote>
<blockquote>

```bash
$ ./mvnw.cmd verify -f superconductor/h2db/pom.xml
```
</blockquote>
                    </blockquote>
                </details>
            </blockquote>
        </details>
        <details><summary>gradle</summary>
            <blockquote>
                <details><summary>unix</summary>
                    <blockquote>
<blockquote>

```bash
$ ./gradlew :superconductor-app-h2db:test :superconductor-app-h2db:check --rerun-tasks
```
</blockquote>
                    </blockquote>
                </details>
                <details><summary>windows</summary>
                    <blockquote>
<blockquote>

```bash
$ ./gradlew.bat :superconductor-app-h2db:test :superconductor-app-h2db:check --rerun-tasks
```
</blockquote>
                    </blockquote>
                </details>
            </blockquote>
        </details>
    </blockquote>
</details>
<details><summary>sqlite</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>
                <details><summary>unix</summary>
                    <blockquote>
<blockquote>

```bash
$ ./mvnw verify -f superconductor/sqlite/pom.xml
```
</blockquote>
                    </blockquote>
                </details>
                <details><summary>windows</summary>
                    <blockquote>
<blockquote>

```bash
$ ./mvnw.cmd verify -f superconductor/sqlite/pom.xml
```
</blockquote>
                    </blockquote>
                </details>
            </blockquote>
        </details>
        <details><summary>gradle</summary>
            <blockquote>
                <details><summary>unix</summary>
                    <blockquote>
<blockquote>

```bash
$ ./gradlew :superconductor-app-sqlite:test :superconductor-app-sqlite:check --rerun-tasks
```
</blockquote>
                    </blockquote>
                </details>
                <details><summary>windows</summary>
                    <blockquote>
<blockquote>

```bash
$ ./gradlew.bat :superconductor-app-sqlite:test :superconductor-app-sqlite:check --rerun-tasks
```
</blockquote>
                    </blockquote>
                </details>
            </blockquote>
        </details>
    </blockquote>
</details>
<details><summary>redis</summary>
    <blockquote>
        <details><summary>maven</summary>
            <blockquote>
                <details><summary>unix</summary>
                    <blockquote>
                        <blockquote>

```bash
$ ./mvnw verify -f superconductor/redis/pom.xml
```
</blockquote>
                    </blockquote>
                </details>
                <details><summary>windows</summary>
                    <blockquote>
                        <blockquote>

```bash
$ ./mvnw.cmd verify -f superconductor/redis/pom.xml
```
</blockquote>
                    </blockquote>
                </details>
            </blockquote>
        </details>
        <details><summary>gradle</summary>
            <blockquote>
                <details><summary>unix</summary>
                    <blockquote>

```bash
$ ./gradlew :superconductor-app-redis:test :superconductor-app-redis:check --rerun-tasks
```
</blockquote>

</details>
                <details><summary>windows</summary>
                    <blockquote>

```bash
$ ./gradlew.bat :superconductor-app-redis:test :superconductor-app-redis:check --rerun-tasks
```
<blockquote>

</blockquote>
                    </blockquote>
                </details>
            </blockquote>
        </details>
    </blockquote>
</details>

----

### Run SuperConductor (4 options)

#### 1.  Docker + Docker Compose
##### Confirm minimal docker requirements
    $ docker --version
>     Docker version 27.5.0
    $ docker compose version
>     Docker Compose version v2.32.4

###### _note: Confirmed compatible with Docker 27.0.3 and Docker Compose version v2.28.1 or higher.  Earlier versions are at the liability of the developer/administrator_

----

##### Dockerize project / Build Supercondcutor Docker images

<details><summary>sqlite</summary>
    <blockquote>
        <details><summary>unix</summary>
            <blockquote>

    $ ./mvnw clean install -Dmaven.test.skip=true
    $ ./mvnw -N wrapper:wrapper
    $ ./mvnw spring-boot:build-image -f superconductor/sqlite/pom.xml -Pdev_ws -Dmaven.test.skip=true
</blockquote>
        </details>
        <details><summary>windows</summary>
            <blockquote>

    $ ./mvnw.cmd clean install -Dmaven.test.skip=true
    $ ./mvnw.cmd -N wrapper:wrapper
    $ ./mvnw.cmd spring-boot:build-image -f superconductor/sqlite/pom.xml -Pdev_ws -Dmaven.test.skip=true
</blockquote>
        </details>
    </blockquote>
</details>
<details><summary>redis</summary>
    <blockquote>
        <details><summary>unix</summary>
            <blockquote>

    $ ./mvnw clean install -Dmaven.test.skip=true
    $ ./mvnw -N wrapper:wrapper
    $ ./mvnw spring-boot:build-image -f superconductor/redis/pom.xml -Pdev_ws -Dmaven.test.skip=true
</blockquote>
        </details>
        <details><summary>windows</summary>
            <blockquote>

    $ ./mvnw.cmd clean install -Dmaven.test.skip=true
    $ ./mvnw.cmd -N wrapper:wrapper
    $ ./mvnw.cmd spring-boot:build-image -f superconductor/redis/pom.xml -Pdev_ws -Dmaven.test.skip=true
</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>mysql</summary>
    <blockquote>
        <details><summary>unix</summary>
            <blockquote>

    $ ./mvnw clean install -Dmaven.test.skip=true
    $ ./mvnw -N wrapper:wrapper
    $ ./mvnw spring-boot:build-image -f superconductor/mysql/pom.xml -Pdev_ws -Dmaven.test.skip=true
</blockquote>
        </details>
        <details><summary>windows</summary>
            <blockquote>

    $ ./mvnw.cmd clean install -Dmaven.test.skip=true
    $ ./mvnw.cmd -N wrapper:wrapper
    $ ./mvnw.cmd spring-boot:build-image -f superconductor/mysql/pom.xml -Pdev_ws -Dmaven.test.skip=true
</blockquote>
        </details>
    </blockquote>
</details>

###### _note: Superconductor spring boot docker uses [buildpacks](https://buildpacks.io/) ([preferential over Dockerfile](https://reflectoring.io/spring-boot-docker/))_

----

##### Start docker containers

<details><summary>WS/HTTP</summary>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/sqlite/docker-compose-dev_ws.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-dev_ws.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-dev_ws.yml up -d && dcls | grep 'superconductor-app-sqlite' | awk '{print $1}' | xargs docker logs -f
</blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/redis/docker-compose-dev_ws.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-dev_ws.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-dev_ws.yml up -d && dcls | grep 'superconductor-app-redis' | awk '{print $1}' | xargs docker logs -f
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/mysql/docker-compose-dev_ws.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-dev_ws.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-dev_ws.yml up -d && dcls | grep 'superconductor-app-mysql' | awk '{print $1}' | xargs docker logs -f

</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>

###### (*optionally edit [superconductor/docker-compose-dev_wss.yml](superconductor/docker-compose-dev_wss.yml?plain=1#L10,L32,L36-L37) parameters as applicable.*)
</blockquote>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/sqlite/docker-compose-dev_wss.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-dev_wss.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-dev_wss.yml up -d && dcls | grep 'superconductor-app-sqlite' | awk '{print $1}' | xargs docker logs -f
</blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/redis/docker-compose-dev_wss.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-dev_wss.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-dev_wss.yml up -d && dcls | grep 'superconductor-app-redis' | awk '{print $1}' | xargs docker logs -f
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/mysql/docker-compose-dev_wss.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-dev_wss.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-dev_wss.yml up -d && dcls | grep 'superconductor-app-mysql' | awk '{print $1}' | xargs docker logs -f

</blockquote>
        </details>
    </blockquote>
</details>

----

##### Stop docker containers

<details><summary>WS/HTTP</summary>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-dev_ws.yml stop 
</blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-dev_ws.yml stop 
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-dev_ws.yml stop 
</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-dev_wss.yml stop 
</blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-dev_wss.yml stop 
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-dev_wss.yml stop 
</blockquote>
        </details>
    </blockquote>
</details>

----  

##### Remove docker containers

<details><summary>WS/HTTP</summary>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-dev_ws.yml down --remove-orphans
</blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-dev_ws.yml down --remove-orphans
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-dev_ws.yml down --remove-orphans

</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-dev_wss.yml down --remove-orphans
</blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-dev_wss.yml down --remove-orphans
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-dev_wss.yml down --remove-orphans

</blockquote>
        </details>
    </blockquote>
</details>  

----

### 2.  Run locally using maven spring-boot:run

<details><summary>WS/HTTP</summary>
    <blockquote>
        <details><summary>sqlite</summary>
            <blockquote>
                <details><summary>maven</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
$ ./mvnw spring-boot:run -f superconductor/sqlite/pom.xml -P local_ws -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=ws://localhost:5555"
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
$ ./mvnw.cmd spring-boot:run -f superconductor/sqlite/pom.xml -P local_ws -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=ws://localhost:5555"
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
                <details><summary>gradle</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
$ ./gradlew superconductor-app-sqlite:bootRunLocalws -Pserver.port=5555 -Psuperconductor.relay.url=ws://localhost:5555
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
$ ./gradlew.bat superconductor-app-sqlite:bootRunLocalws -Pserver.port=5555 -Psuperconductor.relay.url=ws://localhost:5555
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
            </blockquote>
        </details>
        <details><summary>h2db</summary>
            <blockquote>
                <details><summary>maven</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
$ ./mvnw spring-boot:run -f superconductor/h2db/pom.xml -P local_ws -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=ws://localhost:5555"
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
$ ./mvnw.cmd spring-boot:run -f superconductor/h2db/pom.xml -P local_ws -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=ws://localhost:5555"
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
                <details><summary>gradle</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
$ ./gradlew superconductor-app-h2db:bootRunLocalws -Pserver.port=5555 -Psuperconductor.relay.url=ws://localhost:5555
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
$ ./gradlew.bat superconductor-app-h2db:bootRunLocalws -Pserver.port=5555 -Psuperconductor.relay.url=ws://localhost:5555
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
            </blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>
                <details><summary>maven</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml up -d && dcls | grep 'superconductor-db-redis-local' | awk '{print $1}' | xargs docker logs -f

(start superconductor redis)
$ ./mvnw spring-boot:run -f superconductor/redis/pom.xml -P local_ws -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=ws://localhost:5555"

(stop redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml stop && docker compose -f superconductor/redis/docker-compose-local_ws.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml up -d

(start superconductor redis)
$ ./mvnw.cmd spring-boot:run -f superconductor/redis/pom.xml -P local_ws -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=ws://localhost:5555"

(stop redis docker container) 
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml stop
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
                <details><summary>gradle</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml up -d && dcls | grep 'superconductor-db-redis-local' | awk '{print $1}' | xargs docker logs -f

(start superconductor redis)
$ ./gradlew superconductor-app-redis:bootRunLocalws -Pserver.port=5555 -Psuperconductor.relay.url=ws://localhost:5555

(stop redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml stop && docker compose -f superconductor/redis/docker-compose-local_ws.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml up -d

(start superconductor redis)
$ ./gradlew.bat superconductor-app-redis:bootRunLocalws -Pserver.port=5555 -Psuperconductor.relay.url=ws://localhost:5555

(stop redis docker container) 
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml stop
$ docker compose -f superconductor/redis/docker-compose-local_ws.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
            </blockquote>
        </details>
    </blockquote>

###### _note: MySql does not have local mode, only dev (see above section [Start docker containers](#start-docker-containers))_
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>
        <details><summary>h2db</summary>
            <blockquote>
                <details><summary>maven</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
$ ./mvnw spring-boot:run -f superconductor/h2db/pom.xml -P local_wss -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=wss://localhost:5555"
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
$ ./mvnw.cmd spring-boot:run -f superconductor/h2db/pom.xml -P local_wss -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=wss://localhost:5555"
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
                <details><summary>gradle</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
$ ./gradlew superconductor-app-h2db:bootRunLocalWss -Pserver.port=5555 -Psuperconductor.relay.url=wss://localhost:5555
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
$ ./gradlew.bat superconductor-app-h2db:bootRunLocalWss -Pserver.port=5555 -Psuperconductor.relay.url=wss://localhost:5555
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
            </blockquote>
        </details>
        <details><summary>redis</summary>
            <blockquote>
                <details><summary>maven</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml up -d && dcls | grep 'superconductor-redis' | awk '{print $1}' | xargs docker logs -f

(start superconductor redis)
$ ./mvnw spring-boot:run -f superconductor/redis/pom.xml -P local_wss -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=wss://localhost:5555"

(stop redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml stop && docker compose -f superconductor/redis/docker-compose-local_wss.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml up -d

(start superconductor redis)
$ ./mvnw.cmd spring-boot:run -f superconductor/redis/pom.xml -P local_wss -Dspring-boot.run.arguments="--server.port=5555 --superconductor.relay.url=wss://localhost:5555"

(stop redis docker container) 
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml stop
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
                <details><summary>gradle</summary>
                    <blockquote>
                        <details><summary>unix</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml up -d && dcls | grep 'superconductor-redis' | awk '{print $1}' | xargs docker logs -f

(start superconductor redis)
$ ./gradlew superconductor-app-redis:bootRunLocalWss -Pserver.port=5555 -Psuperconductor.relay.url=wss://localhost:5555

(stop redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml stop && docker compose -f superconductor/redis/docker-compose-local_wss.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                        <details><summary>windows</summary>
                            <blockquote>

<blockquote>

```bash
(start redis docker container)
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml up -d && dcls | grep 'superconductor-redis' | awk '{print $1}' | xargs docker logs -f

(start superconductor redis)
$ ./gradlew.bat superconductor-app-redis:bootRunLocalWss -Pserver.port=5555 -Psuperconductor.relay.url=wss://localhost:5555

(stop redis docker container) 
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml stop
$ docker compose -f superconductor/redis/docker-compose-local_wss.yml down --remove-orphans
```
</blockquote>
</blockquote>
                        </details>
                    </blockquote>
                </details>
            </blockquote>
        </details>
    </blockquote>

###### _note: MySql does not have local mode, only dev (see above section [Start docker containers](#start-docker-containers))_
</details>

----

### 3.  Run locally as executable jar

<details><summary>redis</summary>
    <blockquote>
        <details><summary>unix</summary>
            <blockquote>

###### first, start redis docker container as per section [2. Run locally using maven spring-boot:run](#2--run-locally-using-maven-spring-bootrun), then:
```bash
  $ cd <your_git_home_dir>/superconductor
  $ ./mvnw package spring-boot:repackage -f superconductor/redis/pom.xml -Plocal_ws -Dmaven.test.skip=true
  $ java -jar -Dspring.profiles.active=local_ws superconductor/redis/target/superconductor-app-redis-1.16.0.war
```
</blockquote>
        </details>
        <details><summary>microsoft</summary>
            <blockquote>

###### first, start redis docker container as per section [2. Run locally using maven spring-boot:run](#2--run-locally-using-maven-spring-bootrun), then:
```bash
  $ cd <your_git_home_dir>/superconductor
  $ ./mvnw.cmd package spring-boot:repackage -f superconductor/redis/pom.xml -Plocal_ws -Dmaven.test.skip=true
  $ java -jar -Dspring.profiles.active=local_ws superconductor/redis/target/superconductor-app-redis-1.16.0.war
```
</blockquote>
        </details>
    </blockquote>
</details>
<details><summary>sqlite</summary>
    <blockquote>
        <details><summary>unix</summary>
            <blockquote>

```bash
  $ cd <your_git_home_dir>/superconductor
  $ ./mvnw package spring-boot:repackage -f superconductor/sqlite/pom.xml -Plocal_ws -Dmaven.test.skip=true
  $ java -jar -Dspring.profiles.active=local_ws superconductor/sqlite/target/superconductor-app-sqlite-1.16.0.war
```
</blockquote>
        </details>
        <details><summary>microsoft</summary>
            <blockquote>

###### first, start sqlite docker container as per section [2. Run locally using maven spring-boot:run](#2--run-locally-using-maven-spring-bootrun), then:
```bash
  $ cd <your_git_home_dir>/superconductor
  $ ./mvnw.cmd package spring-boot:repackage -f superconductor/sqlite/pom.xml -Plocal_ws -Dmaven.test.skip=true
  $ java -jar -Dspring.profiles.active=local_ws superconductor/sqlite/target/superconductor-app-sqlite-1.16.0.war
```
</blockquote>
        </details>
    </blockquote>
</details>
<details><summary>h2db</summary>
    <blockquote>
        <details><summary>unix</summary>
            <blockquote>

```bash
  $ cd <your_git_home_dir>/superconductor
  $ ./mvnw package spring-boot:repackage -f superconductor/h2db/pom.xml -Plocal_ws -Dmaven.test.skip=true
  $ java -jar -Dspring.profiles.active=local_ws superconductor/h2db/target/superconductor-app-h2db-1.16.0.war
```
</blockquote>
        </details>
        <details><summary>microsoft</summary>
            <blockquote>

```bash
  $ cd <your_git_home_dir>/superconductor
  $ ./mvnw.cmd package spring-boot:repackage -f superconductor/h2db/pom.xml -Plocal_ws -Dmaven.test.skip=true
  $ java -jar -Dspring.profiles.active=local_ws superconductor/h2db/target/superconductor-app-h2db-1.16.0.war
```
</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>mysql</summary>

###### _MySql does not have local mode, only dev (see above section [Start docker containers](#start-docker-containers))_
</details>

----

### 4.  Run using pre-existing local application-server-container instance (tomcat, etc)
<details>
  <summary>redis</summary>

```bash
  $ cp <your_git_home_dir>/superconductor/superconductor/redis/target/superconductor-app-redis-1.16.0.war <your_container/instance/deployment_directory>
```
</details>
<details>
  <summary>sqlite</summary>

```bash
  $ cp <your_git_home_dir>/superconductor/superconductor/sqlite/target/superconductor-app-sqlite-1.16.0.war <your_container/instance/deployment_directory>
```
</details>
<details>
  <summary>h2db</summary>

```bash
  $ cp <your_git_home_dir>/superconductor/superconductor/h2db/target/superconductor-app-h2db-1.16.0.war <your_container/instance/deployment_directory>
```
</details>
<details>
  <summary>mysql</summary>

```bash
  $ cp <your_git_home_dir>/superconductor/superconductor/mysql/target/superconductor-app-mysql-1.16.0.war <your_container/instance/deployment_directory>
```
</details>

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

### Viewing stored/saved events 

<details>
  <summary>h2db</summary>

#### H2 DB console (local non-docker development mode): ##

    http://localhost:5555/h2-console/

*user: h2dbuser  
*password: h2dbuserpass

Display all framework table contents (case-sensitive quoted fields/tables when querying):

    --select id, pub_key, session_id, challenge from auth;
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

</details>

<details><summary>redis</summary>
    <blockquote>
        <details><summary>local docker insight-browser instance</summary>
            <blockquote>

```bash
  $ docker pull redis/redisinsight
  $ docker run -d --name redisinsight -p 5540:5540 redis/redisinsight:latest
```

Next, open browser URL http://localhost:5540 and configure a connection to http://localhost:8081
</blockquote>
        </details>
    </blockquote>
    <blockquote>
        <details><summary>Download insight</summary>
            <blockquote>

Download [redis insight](https://redis.io/downloads/#insight) standalone application and configure a connection to http://localhost:8081
</blockquote>
        </details>
    </blockquote>
</details>

<hr style="border:2px solid grey">

### Adding new/custom events to SuperConductor

For Nostr clients generating canonical Nostr JSON (as defined in [NIP01 spec: Basic protocol flow description, Events, Signatures and Tags](https://nostr-nips.com/nip-01)), SuperConductor will automatically recognize those JSON events- including their database storage, retrieval and subscriber notification.  No additional work or customization is necessary.
<br>
<hr style="border:2px solid grey">

### Adding new/custom tags to SuperConductor

SuperConductor supports any _**generic**_ tags automatically.  Otherwise, if custom tag structure is required, simply implement the [`TagPlugin`](lib/src/main/java/com/prosilion/superconductor/plugin/tag/TagPlugin.java) interface (see [EventTagPlugin](lib/src/main/java/com/prosilion/superconductor/plugin/tag/EventTagPlugin.java) example) and your tag will automatically get included by SuperConductor after rebuilding and redeploying.

