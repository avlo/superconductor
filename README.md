```java
███████╗██╗   ██╗██████╗ ███████╗██████╗  ██████╗ ██████╗ ███╗   ██╗██████╗ ██╗   ██╗ ██████╗████████╗ ██████╗ ██████╗
██╔════╝██║   ██║██╔══██╗██╔════╝██╔══██╗██╔════╝██╔═══██╗████╗  ██║██╔══██╗██║   ██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗
███████╗██║   ██║██████╔╝█████╗  ██████╔╝██║     ██║   ██║██╔██╗ ██║██║  ██║██║   ██║██║        ██║   ██║   ██║██████╔╝
╚════██║██║   ██║██╔═══╝ ██╔══╝  ██╔══██╗██║     ██║   ██║██║╚██╗██║██║  ██║██║   ██║██║        ██║   ██║   ██║██╔══██╗
███████║╚██████╔╝██║     ███████╗██║  ██║╚██████╗╚██████╔╝██║ ╚████║██████╔╝╚██████╔╝╚██████╗   ██║   ╚██████╔╝██║  ██║
╚══════╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═════╝  ╚═════╝  ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
```
# Java Nostr-Relay Framework & Web Application

### Supported Nips
  - [NIP-01](https://nostr-nips.com/nip-01) (Basic protocol & Standard Tags)
  - [NIP-02](https://nostr-nips.com/nip-02) (Contact List and Petnames)
  - [NIP-10](https://nostr-nips.com/nip-10) (Marked "e" tags)
  - [NIP-11](https://nostr-nips.com/nip-11) (Relay Information Document)
  - [NIP-12](https://nostr-nips.com/nip-12) (Generic Tag Queries)
  - [NIP-14](https://nostr-nips.com/nip-14) (Subject tag in Text events)
  - [NIP-16](https://nostr-nips.com/nip-16) (Event treatment)
  - [NIP-19](https://nostr-nips.com/nip-19) (Bech-32 encoded entities)
  - [NIP-20](https://nostr-nips.com/nip-20) (Command Results)
  - [NIP-21](https://nostr-nips.com/nip-21) (URI scheme)
  - [NIP-22](https://nostr-nips.com/nip-22) (Event "created_at" limits)
  - [NIP-31](https://nostr-nips.com/nip-31) (Unknown event kinds)
  - [NIP-33](https://nostr-nips.com/nip-33) (Parameterized Replaceable Events)
  - [NIP-57](https://nostr-nips.com/nip-57) (Lightning Zaps)
  - [NIP-99](https://nostr-nips.com/nip-99) (Classified Listings)
  - used by [Barchetta](https://github.com/avlo/barchetta) Smart-Contract Negotiation Protocol (in progress) atop [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin) [Lightning-Network](https://en.wikipedia.org/wiki/Lightning_Network) [RGB](https://rgb.tech/)

#### In-Progress
  - [NIP-15](https://nostr-nips.com/nip-15) (Nostr Marketplace)
    - used by [Barchetta](https://github.com/avlo/barchetta) Smart-Contract Negotiation Protocol (in progress) atop [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin) [Lightning-Network](https://en.wikipedia.org/wiki/Lightning_Network) [RGB](https://rgb.tech/)
    
----
### Normal/Production Mode (for most users) Instructions:
#### Confirm minimal docker requirements

    $ docker --version
>     Docker version 27.0.3
    $ docker compose version
>     Docker Compose version v2.28.1

(Download links for the above)
- [Docker](https://hub.docker.com/_/docker) 27.0.3
- [Docker Compose](https://docs.docker.com/compose/install/) v2.28.1

----

#### Download Superconductor Docker Image from [hub.docker](https://hub.docker.com/repository/docker/avlo/superconductor-app/tags)
    $ docker pull avlo/superconductor-app:1.7.1

----

#### Download Docker-Compose configuration file:

[docker-compose.yml](docker-compose.yml)

----

#### Run SuperConductor
    $ docker compose -f /<path>/<to>/docker-compose.yml up -d

Superconductor is now ready to use.

----

##### Stop docker containers
    $ docker compose -f docker-compose.yml stop superconductor-app superconductor-db

##### Remove docker containers
    $ docker compose -f docker-compose.yml down --remove-orphans

<hr style="border:2px solid grey">

#### [Development Mode Instructions](DEVELOPMENT.md)