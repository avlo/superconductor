# Nostr Relay, framework + applicaton
- simple & clean, built atop Java Spring (Boot 3.1.5) & Spring WebSockets
- designed and implemented using [SOLID OO](https://www.digitalocean.com/community/conceptual-articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design) principles, valuing efficiency & ease of:
  - extensibility
  - customization
  - testing

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

## Build and run project

    $ mvn spring-boot:run

Service will be available after the following console/log MessageBroker message:

>    INFO 119355 --- [MessageBroker-1] o.s.w.s.c.WebSocketMessageBrokerStats    : WebSocketSession[3 current WS(3)-HttpStream(0)-HttpPoll(0), 3 total, 0 closed abnormally (0 connect failure, 0 send limit, 0 transport error)], stompSubProtocol[processed CONNECT(3)-CONNECTED(3)-DISCONNECT(0)], stompBrokerRelay[null], inboundChannel[pool size = 8, active threads = 0, queued tasks = 0, completed tasks = 18], outboundChannel[pool size = 3, active threads = 0, queued tasks = 0, completed tasks = 3], sockJsScheduler[pool size = 1, active threads = 1, queued tasks = 0, completed tasks = 0]

## Connect web-client to relay

    localhost:8080
