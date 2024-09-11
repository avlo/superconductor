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
  - [NIP-03](https://nostr-nips.com/nip-03) (OpenTimestamps Attestations for Events)
  - [NIP-04](https://nostr-nips.com/nip-04) (Encrypted Direct Message)
  - [NIP-05](https://nostr-nips.com/nip-05) (DNS-based internet identifiers)
  - [NIP-06](https://nostr-nips.com/nip-06) (Basic key derivation from mnemonic seed phrase)
  - [NIP-07](https://nostr-nips.com/nip-07) (window.nostr capability for web browsers)
  - [NIP-08](https://nostr-nips.com/nip-08) (Handling Mentions)
  - [NIP-10](https://nostr-nips.com/nip-10) (Marked "e" tags)
  - [NIP-11](https://nostr-nips.com/nip-11) (Relay Information Document)
  - [NIP-12](https://nostr-nips.com/nip-12) (Generic Tag Queries)
  - [NIP-14](https://nostr-nips.com/nip-14) (Subject tag in Text events)
  - [NIP-16](https://nostr-nips.com/nip-16) (Event treatment)
  - [NIP-18](https://nostr-nips.com/nip-18) (Reposts)
  - [NIP-19](https://nostr-nips.com/nip-19) (Bech-32 encoded entities)
  - [NIP-20](https://nostr-nips.com/nip-20) (Command Results)
  - [NIP-21](https://nostr-nips.com/nip-21) (URI scheme)
  - [NIP-22](https://nostr-nips.com/nip-22) (Event "created_at" limits)
  - [NIP-25](https://nostr-nips.com/nip-25) (Reactions)
  - [NIP-27](https://nostr-nips.com/nip-27) (Text Note References)
  - [NIP-28](https://nostr-nips.com/nip-28) (Public Chat)
  - [NIP-30](https://nostr-nips.com/nip-30) (Custom Emoji)
  - [NIP-31](https://nostr-nips.com/nip-31) (Unknown event kinds)
  - [NIP-33](https://nostr-nips.com/nip-33) (Parameterized Replaceable Events)
  - [NIP-36](https://nostr-nips.com/nip-36) (Sensitive Content / Content Warning)
  - [NIP-38](https://nostr-nips.com/nip-38) (User Statuses)
  - [NIP-39](https://nostr-nips.com/nip-39) (External Identities in Profiles)
  - [NIP-42](https://nostr-nips.com/nip-42) (Authentication of clients to relays)
  - [NIP-46](https://nostr-nips.com/nip-46) (Nostr Connect)
  - [NIP-48](https://nostr-nips.com/nip-48) (Proxy Tags)
  - [NIP-52](https://nostr-nips.com/nip-52) (Calendar Events)
  - [NIP-53](https://nostr-nips.com/nip-53) (Live Activities)
  - [NIP-56](https://nostr-nips.com/nip-56) (Reporting)
  - [NIP-57](https://nostr-nips.com/nip-57) (Lightning Zaps)
  - [NIP-58](https://nostr-nips.com/nip-58) (Badges)
  - [NIP-89](https://nostr-nips.com/nip-89) (Recommended Application Handlers)
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
    $ docker pull avlo/superconductor:1.7.5

----

#### Download Docker-Compose configuration file _(and optionally [edit various parameters](docker-compose-prod.yml?plain=1#L10,32,L36-L37) as desired)_:

[docker-compose-prod.yml](docker-compose-prod.yml)

----

#### Run SuperConductor
    $ docker compose -f /<path>/<to>/docker-compose-prod.yml up -d

Superconductor is now ready to use.

----

##### Stop docker containers
    $ docker compose -f docker-compose-prod.yml stop superconductor superconductor-db

##### Remove docker containers
    $ docker compose -f docker-compose-prod.yml down --remove-orphans

<hr style="border:2px solid grey">

#### [Development Mode Instructions](DEVELOPMENT.md)