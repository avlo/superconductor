# Java Nostr-Relay Framework & Web Application
- Simple.  Clean.  OO.
  - Java 20
  - Spring WebSockets
  - Spring Boot 3.x
  - Event/Message [nostr-java](https://github.com/tcheeric/nostr-java) library by tcheeric
    
- core [SOLID OO](https://www.digitalocean.com/community/conceptual-articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design) principles, providing ease of:
  - understandability
  - extensibility
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
    $ mvn clean install

then [setup requisite nostr-java properties files](https://github.com/tcheeric/nostr-client/?tab=readme-ov-file#setup)

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

##### (Optional Use) bundled web-client URLs for convenience/dev-testing/etc

  http://localhost:5555/NIP01.html

  http://localhost:5555/NIP99.html

  http://localhost:5555/REQ.html
